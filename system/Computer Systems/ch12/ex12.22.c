#include "../lib/csapp.h"

#define MAXCONN 64

void command(void);

int main(int argc, char **argv)
{
    int listenfd, connfd, connfds[MAXCONN], n_connfd = 0, i, max_connfd,
        n_read;
    rio_t rios[MAXCONN];
    socklen_t clientlen;
    struct sockaddr_storage clientaddr;
    fd_set read_set, ready_set;
    char buf[MAXLINE];

    listenfd = Open_listenfd(argv[1]);
    FD_ZERO(&read_set);
    FD_SET(STDIN_FILENO, &read_set);
    FD_SET(listenfd, &read_set);
    max_connfd = listenfd;

    while (1) {
        ready_set = read_set;
        Select(max_connfd+1, &ready_set, NULL, NULL, NULL);
        if (FD_ISSET(STDIN_FILENO, &ready_set))
            command();
        if (FD_ISSET(listenfd, &ready_set)) {
            clientlen = sizeof(struct sockaddr_storage);
            connfd = Accept(listenfd, (SA *)&clientaddr, &clientlen);
            connfds[n_connfd] = connfd;
            Rio_readinitb(&rios[n_connfd], connfd);
            FD_SET(connfd, &read_set);
            if (connfd > max_connfd)
                max_connfd = connfd;
            ++n_connfd;
            continue;
        }
        for (i = 0; i < n_connfd; ++i)
            if (FD_ISSET(connfds[i], &ready_set)) {
                if ((n_read = Rio_readlineb(&rios[i], buf, MAXBUF)) > 0)
                    Rio_writen(connfds[i], buf,n_read);
            }
    }
}

void command(void)
{
    char buf[MAXLINE];
    if (!Fgets(buf, MAXLINE, stdin))
        exit(0);
    printf("%s", buf);
}
