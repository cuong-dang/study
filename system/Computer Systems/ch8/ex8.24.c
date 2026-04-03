#include "../lib/csapp.h"

#define N 2

int main() {
        int status, i;
        pid_t pid;

        for (i = 0; i < N; i++) {
                if ((pid = Fork()) == 0) {
                        *(int *) i = i;
                }
        }
        while ((pid = waitpid(-1, &status, 0)) > 0) {
                if (WIFEXITED(status))
                        printf("child %d terminated normally\n", pid);
                else
                        printf("child %d terminated abnormally: %s\n",
                                pid, strsignal(WTERMSIG(status)));
        }

        if (errno != ECHILD)
                unix_error("waitpid error");
        exit(0);
}
