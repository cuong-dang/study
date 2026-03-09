#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define PROGNAME "wgrep"

void grep(char *s, FILE *fp);

int main(int argc, char *argv[]) {
        int i;
        FILE *fp;

        if (argc < 2) {
                printf("%s: searchterm [file ...]\n", PROGNAME);
                return 1;
        }
        if (argc == 2) {
                grep(argv[1], stdin);
        } else {
                for (i = 2; i < argc; i++) {
                        if ((fp = fopen(argv[i], "r")) == NULL) {
                                printf("%s: cannot open file\n", PROGNAME);
                                exit(1);
                        }
                        grep(argv[1], fp);
                }
        }
        return 0;
}

void grep(char *s, FILE *fp) {
        char *line = NULL;
        size_t linecap = 0;

        while (getline(&line, &linecap, fp) > 0) {
                if (strstr(line, s)) {
                        printf("%s", line);
                }
        }
        if (ferror(fp)) {
                printf("%s: error reading file\n", PROGNAME);
                exit(1);
        }
}
