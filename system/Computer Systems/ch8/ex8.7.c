#include "../lib/csapp.h"

void sigint_handler(int sig);

int main(int argc, char *argv[]) {
        char *errptr;
        unsigned int sleep_dur, unslept;

        if (signal(SIGINT, sigint_handler) == SIG_ERR) {
                unix_error("signal error");
        }
        sleep_dur = (unsigned int) strtol(argv[1], &errptr, 10);
        unslept = sleep(sleep_dur);
        printf("\nSlept for %d of %d seconds.\n",
                sleep_dur - unslept, sleep_dur);
        return 0;
}

void sigint_handler(int sig) {
        return;
}
