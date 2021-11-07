#include <stdio.h>
#include "csapp.h"

/* Recommended max cache and object sizes */
#define MAX_CACHE_SIZE 1049000
#define MAX_OBJECT_SIZE 102400

/* You won't lose style points for including this long line in your code */
static const char *user_agent_hdr = "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:10.0.3) Gecko/20120305 Firefox/10.0.3\r\n";

typedef struct {
    unsigned long num_requests_since;
    char key[MAXBUF];
    int length;
    char *payload;
} cache_object;

cache_object *cache[MAX_CACHE_SIZE];
int cached_bytes = 0;
int cached_objects = 0;
int readcount = 0;
sem_t mutex, w;

void* serve(void* vargp);
int read_headers(rio_t *rp, char *method, char *uri, char *host,
        char *proxy_headers);
void bad_request(int connfd);
cache_object *lookup_cache(char *key);
void inc_request_count(void);
void sigpipe_handler(int sig);

int main(int argc, char **argv)
{
    int listenfd, connfd;
    socklen_t clientlen;
    struct sockaddr_storage clientaddr;
    char host[MAXBUF], port[MAXBUF];
    pthread_t tid;

    Signal(SIGPIPE, sigpipe_handler);
    Sem_init(&mutex, 0, 1);
    Sem_init(&w, 0, 1);

    listenfd = Open_listenfd(argv[1]);
    printf("Listening on port %s\n", argv[1]);
    while (1) {
        clientlen = sizeof(clientaddr);
        connfd = Accept(listenfd, (SA *)&clientaddr, &clientlen);
        Getnameinfo((SA *)&clientaddr, clientlen,
                host, MAXBUF, port, MAXBUF, 0);
        printf("Accepted connfd %d\n", connfd);
        Pthread_create(&tid, NULL, serve, (void *)connfd);
    }
    return 0;
}

void *serve(void *vargp)
{
    rio_t rio;
    int connfd = (int)vargp, rc, clientfd, n, cache_hit = 0, object_size = 0,
            cachable = 1;
    char method[MAXBUF], uri[MAXBUF], host[MAXBUF], proxy_headers[MAXBUF],
            port[MAXBUF], buf[MAXLINE], key[MAXBUF],
            cache_buf[MAX_OBJECT_SIZE];
    cache_object *op;

    Pthread_detach(Pthread_self());
    Rio_readinitb(&rio, connfd);
    if ((rc = read_headers(&rio, method, uri, host, proxy_headers)) != 0) {
        printf("connfd %d made bad request, errcode %d\n", connfd, rc);
        bad_request(connfd);
        Close(connfd);
        return NULL;
    }
    printf("connfd requested %s%s\n", host, uri);
    if (sscanf(host, "%[^:]:%s", host, port) != 2)
        strcpy(port, "80");

    /* search cache */
    strcpy(key, host);
    strcat(key, uri);
    P(&mutex);
    ++readcount;
    if (readcount == 1)
        P(&w);
    V(&mutex);
    if ((op = lookup_cache(key))) {
        /* cache hit */
        cache_hit = 1;
        printf("  Cache hit! Serving from cache\n");
        Rio_writen(connfd, op->payload, op->length);
    } else {
        /* cache miss */
        printf("  Cache miss! Fetch from remote host\n");
        clientfd = Open_clientfd(host, port);
        Rio_readinitb(&rio, clientfd);
        Rio_writen(clientfd, proxy_headers, strlen(proxy_headers));
        while ((n = Rio_readlineb(&rio, buf, MAXLINE)) > 0) {
            Rio_writen(connfd, buf, n);
            if (cachable && object_size+n < MAX_OBJECT_SIZE) {
                memcpy(cache_buf+object_size, buf, n);
                object_size += n;
            } else
                cachable = 0;
        }
    }
    P(&mutex);
    --readcount;
    if (readcount == 0)
        V(&w);
    V(&mutex);

    /* update cache */
    P(&w);
    inc_request_count();
    if (cache_hit)
        op->num_requests_since = 0;
    else if (cachable) {
        printf("  Caching %s\n", key);
        op = Malloc(sizeof(cache_object));
        op->num_requests_since = 0;
        strcpy(op->key, key);
        op->length = object_size;
        op->payload = Malloc(op->length);
        memcpy(op->payload, cache_buf, op->length);
        cache[cached_objects++] = op;
        cached_bytes += op->length;
    }
    V(&w);
    Close(connfd);
    return NULL;
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
    if (host_set)
        sprintf(proxy_headers, "%sHost: %s\r\n", proxy_headers, host);
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

    if (!host_set)
        return 8;
    sprintf(proxy_headers, "%sConnection: close\r\n", proxy_headers);
    sprintf(proxy_headers, "%sProxy-Connection: close\r\n", proxy_headers);
    sprintf(proxy_headers, "%s%s\r\n", proxy_headers, user_agent_hdr);
    return 0;
}

void bad_request(int connfd)
{
    char buf[MAXLINE];

    sprintf(buf, "HTTP/1.0 200 OK\r\n");
    Rio_writen(connfd, buf, strlen(buf));
    sprintf(buf, "Content-type: text/html\r\n\r\n");
    Rio_writen(connfd, buf, strlen(buf));
    sprintf(buf, "<html><title>400 Bad Request</title></html>\r\n");
    Rio_writen(connfd, buf, strlen(buf));
}

cache_object *lookup_cache(char *key)
{
    int i;

    for (i = 0 ; i < cached_objects; ++i)
        if (strcmp(cache[i]->key, key) == 0)
            return cache[i];
    return NULL;
}

void inc_request_count(void)
{
    int i;

    for (i = 0; i < cached_objects; ++i)
        ++(cache[i]->num_requests_since);
}

void sigpipe_handler(int sig)
{
    /* do nothing */
}
