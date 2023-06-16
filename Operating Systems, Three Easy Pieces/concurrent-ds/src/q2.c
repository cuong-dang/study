#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

#include "ccounter.h"
#include "timex.h"

typedef struct
{
    counter_t *c;
    long count_to;
} ptarg_t;

void *inc_counter(void *arg)
{
    int i;

    ptarg_t *a = (ptarg_t *)arg;
    for (i = 0; i < a->count_to; ++i)
    {
        inc(a->c);
    }
    return NULL;
}

int main(int argc, char *argv[])
{
    long count_to, num_threads;
    counter_t c;
    pthread_t *threads;
    ptarg_t arg;
    struct timeval start, end, delta;
    int i;

    init(&c);
    count_to = strtol(argv[1], NULL, 10);
    num_threads = strtol(argv[2], NULL, 10);
    threads = (pthread_t *)malloc(sizeof(pthread_t) * num_threads);
    arg.count_to = count_to / num_threads;
    arg.c = &c;
    gettimeofday(&start, NULL);
    for (i = 0; i < num_threads; ++i)
    {
        pthread_create(&threads[i], NULL, inc_counter, (void *)&arg);
    }
    for (i = 0; i < num_threads; ++i)
    {
        pthread_join(threads[i], NULL);
    }
    gettimeofday(&end, NULL);
    delta = diff(start, end);
    printf("Counted to: %d\n", c.value);
    printf("Time taken: %lds %dus\n", delta.tv_sec, delta.tv_usec);
    free(threads);
    return 0;
}
