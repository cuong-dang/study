/*
 * tiny.c - A simple, iterative HTTP/1.0 Web server that uses the
 *     GET method to serve static and dynamic content
 */
#include "../../lib/csapp.h"
#include "../../lib/sbuf.h"

#define MAXCONN 64
#define STARTING_NTHREADS 2
#define LONG_TASK_SIMUL_SECS 10

void doit(int fd);
void read_requesthdrs(rio_t *rp);
int parse_uri(char *uri, char *filename, char *cgiargs);
void serve_static(int fd, char *filename, int filesize, int is_head);
void get_filetype(char *filename, char *filetype);
void serve_dynamic(int fd, char *filename, char *cgiargs, int is_head);
void clienterror(int fd, char *cause, char *errnum,
                 char *shortmsg, char *longmsg);
void sigchld_handler(int sig);
void sigpipe_handler(int sig);
void *thread(void *vargp);

int nconns = 0, nthreads = 0;
pthread_t tids[MAXCONN];
sem_t conn_lock;

int main(int argc, char **argv)
{
        int listenfd, connfd, old_nthreads, i;
        char hostname[MAXLINE], port[MAXLINE];
        socklen_t clientlen;
        struct sockaddr_storage clientaddr;
        sbuf_t sbuf;

        Signal(SIGCHLD, sigchld_handler);
        Signal(SIGPIPE, sigpipe_handler);

        /* Check command-line args */
        if (argc != 2) {
                fprintf(stderr, "usage: %s <port>\n", argv[0]);
                exit(1);
        }

        /* Spin up threads */
        Sem_init(&conn_lock, 0, 1);
        sbuf_init(&sbuf, MAXCONN);
        for (i = 0; i < STARTING_NTHREADS; ++i)
            Pthread_create(&tids[nthreads++], NULL, thread, (void *)&sbuf);
        listenfd = Open_listenfd(argv[1]);
        while (1) {
                clientlen = sizeof(clientaddr);
                connfd = Accept(listenfd, (SA *)&clientaddr, &clientlen);
                P(&conn_lock);
                ++nconns;
                if (nthreads == nconns) {
                    printf("Reached %d connections; double to %d threads\n",
                           nconns, 2*nthreads);
                    for (i = 0, old_nthreads = nthreads; i < old_nthreads; ++i)
                        Pthread_create(&tids[nthreads++], NULL, thread,
                                       (void *)&sbuf);
                }
                V(&conn_lock);
                Getnameinfo((SA *)&clientaddr, clientlen, hostname, MAXLINE,
                            port, MAXLINE, 0);
                printf("Accepted connection from (%s, %s)\n", hostname, port);
                sbuf_insert(&sbuf, connfd);
        }
}

void doit(int fd)
{
        int is_static, is_head;
        struct stat sbuf;
        char buf[MAXLINE], method[MAXLINE], uri[MAXLINE], version[MAXLINE];
        char filename[MAXLINE], cgiargs[MAXLINE];
        rio_t rio;

        /* Read request line and headers */
        Rio_readinitb(&rio, fd);
        Rio_readlineb(&rio, buf, MAXLINE);
        // printf("Request headers:\n");
        // printf("%s", buf);
        sscanf(buf, "%s %s %s", method, uri, version);
        if (strcasecmp(method, "GET") && strcasecmp(method, "HEAD")) {
                clienterror(fd, method, "501", "Not implemented",
                            "Tiny does not implement this method");
                return;
        }
        if (!strcasecmp(method, "HEAD")) is_head = 1;
        read_requesthdrs(&rio);

        /* Parse URI from GET request */
        is_static = parse_uri(uri, filename, cgiargs);
        if (stat(filename, &sbuf) < 0) {
                clienterror(fd, filename, "404", "Not found",
                            "Tiny couldn't find this file");
                return;
        }

        if (is_static) { /* Serve static content */
                if (!(S_ISREG(sbuf.st_mode)) || !(S_IRUSR & sbuf.st_mode)) {
                        clienterror(fd, filename, "403", "Forbidden",
                                    "Tiny couldn't read the file");
                        return;
                }
                serve_static(fd, filename, sbuf.st_size, is_head);
        }
        else { /* Serve dynamic content */
                if (!(S_ISREG(sbuf.st_mode)) || !(S_IXUSR & sbuf.st_mode)) {
                        clienterror(fd, filename, "403", "Forbidden",
                                    "Tiny couldn't run the CGI program");
                        return;
                }
                serve_dynamic(fd, filename, cgiargs, is_head);
        }
}

void clienterror(int fd, char *cause, char *errnum,
                 char *shortmsg, char *longmsg)
{
        char buf[MAXLINE];

        /* Print the HTTP response headers */
        sprintf(buf, "HTTP/1.0 %s %s\r\n", errnum, shortmsg);
        Rio_writen(fd, buf, strlen(buf));
        sprintf(buf, "Content-type: text/html\r\n\r\n");
        Rio_writen(fd, buf, strlen(buf));

        /* Print the HTTP response body */
        sprintf(buf, "<html><title>Tiny Error</title>");
        Rio_writen(fd, buf, strlen(buf));
        sprintf(buf, "<body bgcolor=""ffffff"">\r\n");
        Rio_writen(fd, buf, strlen(buf));
        sprintf(buf, "%s: %s\r\n", errnum, shortmsg);
        Rio_writen(fd, buf, strlen(buf));
        sprintf(buf, "<p>%s: %s\r\n", longmsg, cause);
        Rio_writen(fd, buf, strlen(buf));
        sprintf(buf, "<hr><em>The Tiny Web server</em>\r\n");
        Rio_writen(fd, buf, strlen(buf));
}

void read_requesthdrs(rio_t *rp)
{
    char buf[MAXLINE];

    Rio_readlineb(rp, buf, MAXLINE);
    // printf("%s", buf);
    while(strcmp(buf, "\r\n")) {
        Rio_readlineb(rp, buf, MAXLINE);
        // printf("%s", buf);
    }
}

int parse_uri(char *uri, char *filename, char *cgiargs)
{
    char *ptr;

    if (!strstr(uri, "cgi-bin")) {  /* Static content */
	strcpy(cgiargs, "");
	strcpy(filename, ".");
	strcat(filename, uri);
	if (uri[strlen(uri)-1] == '/')
	    strcat(filename, "home.html");
	return 1;
    }
    else {  /* Dynamic content */
	ptr = index(uri, '?');
	if (ptr) {
	    strcpy(cgiargs, ptr+1);
	    *ptr = '\0';
	}
	else
	    strcpy(cgiargs, "");
	strcpy(filename, ".");
	strcat(filename, uri);
	return 0;
    }
}

void serve_static(int fd, char *filename, int filesize, int is_head)
{
    int srcfd, n;
    ssize_t write_stt;
    char *srcp, filetype[MAXLINE], buf[MAXBUF];
    void *riobuf;

    /* Send response headers to client */
    get_filetype(filename, filetype);
    sprintf(buf, "HTTP/1.0 200 OK\r\n");
    Rio_writen(fd, buf, strlen(buf));
    sprintf(buf, "Server: Tiny Web Server\r\n");
    Rio_writen(fd, buf, strlen(buf));
    sprintf(buf, "Content-length: %d\r\n", filesize);
    Rio_writen(fd, buf, strlen(buf));
    sprintf(buf, "Content-type: %s\r\n\r\n", filetype);
    Rio_writen(fd, buf, strlen(buf));
    if (is_head) return;

    /* Send response body to client */
    srcfd = Open(filename, O_RDONLY, 0);
    // srcp = Mmap(0, filesize, PROT_READ, MAP_PRIVATE, srcfd, 0);
    // Close(srcfd);
    // Rio_writen(fd, srcp, filesize);
    // Munmap(srcp, filesize);
    if (!(riobuf = malloc(BUFSIZ))) return;
    while ((n = Rio_readn(srcfd, riobuf, BUFSIZ)) > 0)
        if ((write_stt = write(fd, riobuf, n)) == -1) {
            fprintf(stderr, "warning: %s\n", strerror(errno));
            return;
        }
    free(riobuf);
}

void get_filetype(char *filename, char *filetype)
{
    if (strstr(filename, ".html")) strcpy(filetype, "text/html");
    else if (strstr(filename, ".gif")) strcpy(filetype, "image/gif");
    else if (strstr(filename, ".png")) strcpy(filetype, "image/png");
    else if (strstr(filename, ".jpg")) strcpy(filetype, "image/jpeg");
    else if (strstr(filename, ".mp4")) strcpy(filetype, "video/mp4");
    else strcpy(filetype, "text/plain");
}

void serve_dynamic(int fd, char *filename, char *cgiargs, int is_head)
{
    char buf[MAXLINE], *emptylist[] = { NULL };

    /* Return first part of HTTP response */
    sprintf(buf, "HTTP/1.0 200 OK\r\n");
    Rio_writen(fd, buf, strlen(buf));
    sprintf(buf, "Server: Tiny Web Server\r\n");
    Rio_writen(fd, buf, strlen(buf));

    if (Fork() == 0) { /* Child */
        /* Real server would set all CGI vars here */
        setenv("QUERY_STRING", cgiargs, 1);
        if (is_head) setenv("IS_HEAD", "1", 1);
        Dup2(fd, STDOUT_FILENO); /* Redirect stdout to client */
        Execve(filename, emptylist, environ); /* Run CGI program */
    }
}

void sigchld_handler(int sig)
{
    int pid, status;

    while ((pid = waitpid(-1, &status, WNOHANG | WUNTRACED)) > 0)
        printf("child process %d reaped with exit status %d\n", pid, status);
}

void sigpipe_handler(int sig)
{
    /* do nothing */
}

void *thread(void *vargp)
{
    sbuf_t *sp = (sbuf_t *)vargp;
    int connfd, suicide, old_nthreads, i;
    pthread_t self = Pthread_self();

    while (1) {
        connfd = sbuf_remove(sp);
        doit(connfd);
        Close(connfd);
        sleep(LONG_TASK_SIMUL_SECS);
        P(&conn_lock);
        --nconns;
        if (nconns == 0 && nthreads > STARTING_NTHREADS) {
            printf("No more active connections; halve to %d threads\n",
                   nthreads/ 2);
            for (i = nthreads / 2, old_nthreads = nthreads; i < old_nthreads;
                 ++i) {
                if (tids[i] == self) suicide = 1;
                else Pthread_cancel(tids[i]);
                --nthreads;
            }
            if (suicide) {
                V(&conn_lock);
                Pthread_cancel(self);
            }
        }
        V(&conn_lock);
    }
}
