#include "../lib/csapp.h"

#define IN_FILE "./hello.txt"

int main(int argc, char *argv[]) {
        int fd;
        struct stat file_stat;
        char *filemap;
        int i;

        if ((fd = open(IN_FILE, O_RDWR)) < 0)
                exit(1);
        Fstat(fd, &file_stat);
        filemap = (char *) Mmap(NULL, file_stat.st_size,
                        PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
        *filemap = 'J';
        for (i = 0; i < file_stat.st_size; i++)
                printf("%c", filemap[i]);

        return 0;
}
