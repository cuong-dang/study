#include "../lib/csapp.h"

int main(int argc, char **argv) {
        struct addrinfo *p, *listp, hints;
        struct sockaddr_in *pp;
        char buf[MAXLINE], manbuf[INET_ADDRSTRLEN];
        int rc, flags;

        if (argc != 2) {
                fprintf(stderr, "usage: %s <domain name>\n", argv[0]);
                return 0;
        }

        /* Get a list of addrinfo records */
        memset(&hints, 0, sizeof(struct addrinfo));
        hints.ai_family = AF_INET;
        hints.ai_socktype = SOCK_STREAM;
        if ((rc = getaddrinfo(argv[1], NULL, &hints, &listp)) != 0) {
                fprintf(stderr, "getaddrinfo error: %d\n", gai_strerror(rc));
                return 1;
        }

        /* Walk the list and display each IP address */
        flags = NI_NUMERICHOST;
        for (p = listp; p; p = p->ai_next) {
                Getnameinfo(p->ai_addr, p->ai_addrlen, buf, MAXLINE,
                            NULL, 0, flags);
                printf("expected: %s; ", buf);
                /* Manual conversion without getnameinfo */
                pp = (struct sockaddr_in *)p->ai_addr;
                if (!(inet_ntop(AF_INET, &(pp->sin_addr), manbuf,
                                INET_ADDRSTRLEN))) {
                        fprintf(stderr, "inet_ntop error\n");
                        return 2;
                }
                printf("actual: %s; ", manbuf);
                printf("canonical name: %s\n", p->ai_canonname);
        }

        /* Clean up */
        Freeaddrinfo(listp);

        exit(0);
}
