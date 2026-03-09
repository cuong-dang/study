#include "../lib/csapp.h"

#define TIMEOUT_SECS 5
#define FGETS_MAX_LENGTH 64

jmp_buf buf;

void sigalarm_handler(int sig) {
        longjmp(buf, 1);
}

char *tfgets(char *str, int size, FILE *stream) {
        pid_t pid;

        alarm(TIMEOUT_SECS);
        switch(setjmp(buf)) {
                case 0:
                        return fgets(str, FGETS_MAX_LENGTH, stream);
                case 1:
                        return NULL;
        }
        longjmp(buf, 0);
}

int main() {
        char *input, buf[FGETS_MAX_LENGTH];

        Signal(SIGALRM, sigalarm_handler);
        while (1) {
                printf("Enter something: ");
                if (((input = tfgets(buf, FGETS_MAX_LENGTH, stdin)) != NULL)) {
                        printf("You entered: %s", input);
                } else if (feof(stdin)) {
                        return 0;
                } else {
                        printf("\nToo slow! Try again.\n");
                }
        }
}
