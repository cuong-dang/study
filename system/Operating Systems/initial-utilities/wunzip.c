#include <stdio.h>

#define PROGNAME "wunzip"
#define BUFSIZE 4096

int main(int argc, char *argv[]) {
        int i, j, count;
        FILE *fp;
        char c;

        if (argc < 2) {
                printf("%s: file1 [file2 ...]\n", PROGNAME);
                return 1;
        }
        for (i = 1; i < argc; i++) {
                if ((fp = fopen(argv[i], "r")) == NULL) {
                        printf("%s: cannot open file\n", PROGNAME);
                        return 1;
                }
                while (fread(&count, 1, 4, fp) > 0) {
                        fread(&c, 1, 1, fp);
                        for (j = 0; j < count; ++j) {
                                fwrite(&c, 1, 1, stdout);
                        }
                }
                if (ferror(fp)) {
                        printf("%s: error reading file\n", PROGNAME);
                        return 1;
                }
        }
        return 0;
}
