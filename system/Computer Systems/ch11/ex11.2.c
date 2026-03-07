#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>

int main(int argc, char **argv) {
        char dest[INET_ADDRSTRLEN], *endptr;
        uint32_t n;

        if (argc != 2) {
                printf("invalid number of arguments\n");
                return 1;
        }
        n = ntohl(strtol(argv[1], &endptr, 16));

        if (*endptr != '\0') {
                printf("fail to parse number\n");
                return 2;
        }

        if (inet_ntop(AF_INET, &n, dest, INET_ADDRSTRLEN) == NULL) {
                printf("fail to convert\n");
                return 3;
        }
        printf("%s\n", dest);
        return 0;
}
