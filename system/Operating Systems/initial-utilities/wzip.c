#include <stdio.h>

#define PROGNAME "wzip"
#define BUFSIZE 4096

void write(char head, int count);

int main(int argc, char *argv[]) {
        FILE *fp;
        ssize_t i, j, count = 0, n;
        char head = '\0', s[BUFSIZE];

        if (argc < 2) {
                printf("%s: file1 [file2 ...]\n", PROGNAME);
                return 1;
        }
        for (i = 1; i < argc; i++) {
                if ((fp = fopen(argv[i], "r")) == NULL) {
                        printf("%s: cannot open file\n", PROGNAME);
                        return 1;
                }
                while ((n = fread(s, 1, BUFSIZE, fp)) > 0) {
                        j = 0;
                        while (j < n) {
                                if (head != '\0' && head != s[j]) {
                                        write(head, count);
                                        count = 0;
                                }
                                head = s[j];
                                while (j < n && head == s[j]) {
                                        count++;
                                        j++;
                                }
                        }
                }
                if (ferror(fp)) {
                        printf("%s: error reading file\n", PROGNAME);
                        return 1;
                }
        }
        write(head, count);
        return 0;
}

void write(char head, int count) {
        fwrite(&count, 4, 1, stdout);
        fwrite(&head, 1, 1, stdout);
}
