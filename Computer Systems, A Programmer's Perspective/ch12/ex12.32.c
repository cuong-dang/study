#include "../lib/csapp.h"

#define TIMEOUT_SECS 5

char *tfgets(char *s, int size, FILE *stream)
{
    struct timeval tv;
    int select_rv;
    fd_set read_set;

    FD_ZERO(&read_set);
    FD_SET(0, &read_set);
    tv.tv_sec = TIMEOUT_SECS;
    tv.tv_usec = 0;
    select_rv = Select(1, &read_set, NULL, NULL, &tv);
    if (select_rv != 0) {
        fgets(s, size, stdin);
        return s;
    }
    return NULL;
}
