#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>

int main(int argc, char **argv) {
        uint32_t n;

        if (argc != 2) {
                printf("invalid number of arguments\n");
                return 1;
        }

        if (inet_pton(AF_INET, argv[1], &n) != 1) {
                printf("fail to convert\n");
                return 2;
        }
        printf("0x%x\n", htonl(n));
        return 0;
}
