#include "../lib/csapp.h"

#define TIMEOUT_SECS 5
#define JMPED 1

static void sigchld_handler(int sig);
static jmp_buf env_buf;

char *tfgets(char *s, int size, FILE *stream)
{
    Signal(SIGCHLD, sigchld_handler);
    if (setjmp(env_buf) == JMPED)
        return NULL;
    if (Fork() == 0) {
        sleep(TIMEOUT_SECS);
        exit(0);
    }
    fgets(s, MAXLINE, stdin);
    return s;
}

static void sigchld_handler(int sig)
{
    while (waitpid(-1, NULL, WNOHANG | WUNTRACED) > 0)
        ;
    longjmp(env_buf, JMPED);
}
