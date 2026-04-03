#include "../lib/csapp.h"

int main(int argc, char *argv[]) {
        FILE *file = Fopen(argv[1], "r");
        struct stat file_stat;
        void *filemap;
        int i;

        Fstat(fileno(file), &file_stat);
        filemap = Mmap(NULL, file_stat.st_size, PROT_READ, MAP_SHARED,
                       fileno(file), 0);
        for (i = 0; i < file_stat.st_size; i++)
                printf("%c", *((char *) filemap + i));
        return 0;
}
