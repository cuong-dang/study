#include <sys/time.h>

struct timeval diff(struct timeval start, struct timeval end) {
    struct timeval result;

    result.tv_sec = end.tv_sec - start.tv_sec;
    if (end.tv_usec >= start.tv_usec) {
        result.tv_usec = end.tv_usec - start.tv_usec;
    } else {
        result.tv_usec = 1000000 + end.tv_usec - start.tv_usec;
        --result.tv_sec;
    }
    return result;
}
