#include <stdio.h>

#define BUFSIZE 4096

int main(int argc, char *argv[]) {
        int i;
        FILE *fp;
        char s[BUFSIZE];

        if (argc < 2) {
                return 0;
        }
        for (i = 1; i < argc; i++) {
                if ((fp = fopen(argv[i], "r")) == NULL) {
                        printf("wcat: cannot open file\n");
                        return 1;
                }
                while ((fgets(s, BUFSIZE, fp)) != NULL) {
                        printf("%s", s);
                }
                if (ferror(fp)) {
                        printf("wcat: error reading file\n");
                        return 1;
                }
        }
        return 0;
}
