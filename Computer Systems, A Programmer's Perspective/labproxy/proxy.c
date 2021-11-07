#include <stdio.h>
#include "csapp.h"

/* Recommended max cache and object sizes */
#define MAX_CACHE_SIZE 1049000
#define MAX_OBJECT_SIZE 102400

/* You won't lose style points for including this long line in your code */
static const char *user_agent_hdr = "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:10.0.3) Gecko/20120305 Firefox/10.0.3\r\n";

void serve(int connfd);
int read_headers(rio_t *rp, char *method, char *uri, char *host,
        char *proxy_headers);
void bad_request(int connfd);

int main(int argc, char **argv)
{
    int listenfd, connfd;
    socklen_t clientlen;
    struct sockaddr_storage clientaddr;
    char host[MAXBUF], port[MAXBUF];

    listenfd = Open_listenfd(argv[1]);
    printf("Listening on port %s\n", argv[1]);
    while (1) {
        clientlen = sizeof(clientaddr);
        connfd = Accept(listenfd, (SA *)&clientaddr, &clientlen);
        Getnameinfo((SA *)&clientaddr, clientlen,
                host, MAXBUF, port, MAXBUF, 0);
        printf("Accepted connfd %d\n", connfd);
        serve(connfd);
        Close(connfd);
    }
    return 0;
}

void serve(int connfd)
{
    rio_t rio;
    char method[MAXBUF], uri[MAXBUF], host[MAXBUF], proxy_headers[MAXBUF],
            port[MAXBUF], buf[MAXLINE];
    int rc, clientfd, n;

    Rio_readinitb(&rio, connfd);
    if ((rc = read_headers(&rio, method, uri, host, proxy_headers)) != 0) {
        printf("connfd %d made bad request, errcode %d\n", connfd, rc);
        bad_request(connfd);
        return;
    }
    printf("connfd requested %s%s\n", host, uri);
    if (sscanf(host, "%[^:]:%s", host, port) != 2)
        strcpy(port, "80");
    printf("Requesting headers:\n%s", proxy_headers);
    clientfd = Open_clientfd(host, port);
    Rio_readinitb(&rio, clientfd);
    Rio_writen(clientfd, proxy_headers, strlen(proxy_headers));
    while ((n = Rio_readlineb(&rio, buf, MAXLINE)) > 0)
        Rio_writen(connfd, buf, n);
}

int read_headers(rio_t *rp, char *method, char *uri, char *host,
        char *proxy_headers)
{
    char buf[MAXLINE], raw_uri[MAXBUF], protocol[MAXBUF];
    int host_set = 0;

    if (!Rio_readlineb(rp, buf, MAXLINE))
        return 1;
    if (sscanf(buf, "%s %s %s\n", method, raw_uri, protocol) != 3)
        return 2;
    if (strcasecmp(method, "GET"))
        return 3;
    if (strcasecmp(protocol, "HTTP/1.0") &&
            strcasecmp(protocol, "HTTP/1.1"))
        return 4;

    /* extract uri */
    if (strncmp(raw_uri, "https", 5) == 0)
        return 5;
    if (strncmp(raw_uri, "http", 4) == 0) {
        if (sscanf(raw_uri, "http://%[^/]%s", host, uri) != 2)
            return 6;
        host_set = 1;
    } else
        strcpy(uri, raw_uri);

    sprintf(proxy_headers, "GET %s HTTP/1.0\r\n", uri);
    if (host_set) sprintf(proxy_headers, "%sHost: %s\r\n", proxy_headers, host);
    while (strcmp(buf, "\r\n")) {
        Rio_readlineb(rp, buf, MAXLINE);
        if (strncmp(buf, "Host: ", 6) == 0 && !host_set) {
            if (sscanf(buf, "Host: %s", host) != 1)
                return 7;
            host_set = 1;
            sprintf(proxy_headers, "%sHost: %s\r\n", proxy_headers, host);
            continue;
        }
        if (strncmp(buf, "Connection: ", 12))
            continue;
        if (strncmp(buf, "User-Agent: ", 12))
            continue;
        sprintf(proxy_headers, "%s%s", proxy_headers, buf);
    }

    if (!host_set) return 8;
    sprintf(proxy_headers, "%sConnection: close\r\n", proxy_headers);
    sprintf(proxy_headers, "%sProxy-Connection: close\r\n", proxy_headers);
    sprintf(proxy_headers, "%s%s\r\n", proxy_headers, user_agent_hdr);
    return 0;
}

void bad_request(int connfd)
{
    char buf[MAXLINE];

    sprintf(buf, "HTTP/1.0 400 Bad Request\r\n");
    Rio_writen(connfd, buf, strlen(buf));
    sprintf(buf, "Content-type: text/html\r\n\r\n");
    Rio_writen(connfd, buf, strlen(buf));
    sprintf(buf, "<html><title>400 Bad Request</title></html>\r\n");
    Rio_writen(connfd, buf, strlen(buf));
}
