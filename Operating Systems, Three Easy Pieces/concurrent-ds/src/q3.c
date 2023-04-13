#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "acounter.h"
#include "timex.h"

typedef struct {
    acounter_t *c;
    int thread_id;
    long count_to;
} ptarg_t;

void *inc_counter(void *arg) {
    int i;

    ptarg_t *a = (ptarg_t *) arg;
    for (i = 0; i < a->count_to; ++i) {
        inc(a->c, a->thread_id);
    }
    return NULL;
}

void *monitor(void *arg) {
    acounter_t *c = (acounter_t *) arg;

    printf("Begin monitoring...\n");
    while (1) {
        printf("Current counter's global value: %d...\n", get(c));
        sleep(1);
    }
}

int main(int argc, char *argv[]) {
    long count_to, num_threads, u_threshold;
    acounter_t c;
    pthread_t *threads, t_monitor;
    ptarg_t *args;
    struct timeval start, end, delta;
    int i;

    count_to = strtol(argv[1], NULL, 10);
    num_threads = strtol(argv[2], NULL, 10);
    u_threshold = strtol(argv[3], NULL, 10);
    init(&c, num_threads, u_threshold);
    threads = (pthread_t *) malloc(sizeof(pthread_t) * num_threads);
    args = (ptarg_t *) malloc(sizeof(ptarg_t) * num_threads);
    gettimeofday(&start, NULL);
    for (i = 0; i < num_threads; ++i) {
        args[i].count_to = count_to / num_threads;
        args[i].c = &c;
        args[i].thread_id = i;
        pthread_create(&threads[i], NULL, inc_counter, (void *) &args[i]);
    }
    if (argc == 5 && strtol(argv[4], NULL, 10) == 1) {
        pthread_create(&t_monitor, NULL, monitor, (void *) &c);
    }
    for (i = 0; i < num_threads; ++i) {
        pthread_join(threads[i], NULL);
    }
    gettimeofday(&end, NULL);
    delta = diff(start, end);
    printf("Counted to: %d\n", get_exact(&c));
    printf("Time taken: %lds %dus\n", delta.tv_sec, delta.tv_usec);
    free(args);
    free(threads);
    destroy(&c);
    return 0;
}
