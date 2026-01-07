#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include "glinkedlist.h"
#include "timex.h"
#include "util.h"

#define ADD_OP 0
#define GET_OP 1

typedef struct
{
    glinkedlist_t *g;
    int numops;
    int numkeys;
    int *ops;
} ptarg_t;

int *prep_thread_ops(int numops, double perc_addop);
void run_threads(int numthreads, ptarg_t args[], int lockstrat);
void *run_thread(void *arg);

int main(int argc, char *argv[])
{
    int numops, numkeys, numthreads, numops_per_thread, i;
    double perc_addop;
    ptarg_t *args;
    struct timeval start, end, delta;

    /* Init */
    srand(time(NULL));
    numthreads = strtol(argv[1], NULL, 10);
    numops = strtol(argv[2], NULL, 10);
    numkeys = strtol(argv[3], NULL, 10);
    perc_addop = strtod(argv[4], NULL);
    printf("Args: %d threads, %d ops, %d keys, %.0f%% add\n",
           numthreads, numops, numkeys, perc_addop * 100);
    /* Prep threads & args */
    args = (ptarg_t *)malloc(sizeof(ptarg_t) * numthreads);
    numops_per_thread = numops / numthreads;
    for (i = 0; i < numthreads; ++i)
    {
        args[i].numops = numops_per_thread;
        args[i].numkeys = numkeys;
        args[i].ops = prep_thread_ops(numops / numthreads, perc_addop);
    }
    /* Run */
    printf("Execute with global lock strategy...\n");
    gettimeofday(&start, NULL);
    run_threads(numthreads, args, GLINKEDLIST_LOCKSTRAT_GLOBAL);
    gettimeofday(&end, NULL);
    delta = diff(start, end);
    printf("- Time taken: %lds %dus\n", delta.tv_sec, delta.tv_usec);

    printf("Execute with hand-over-hand lock strategy...\n");
    gettimeofday(&start, NULL);
    run_threads(numthreads, args, GLINKEDLIST_LOCKSTRAT_HOH);
    gettimeofday(&end, NULL);
    delta = diff(start, end);
    printf("- Time taken: %lds %dus\n", delta.tv_sec, delta.tv_usec);

    return 0;
}

int *prep_thread_ops(int numops, double perc_addop)
{
    int *res, i;

    res = (int *)malloc(numops * sizeof(int));
    for (i = 0; i < numops; ++i)
    {
        if (rand() % 100 / 100.0 < perc_addop)
        {
            res[i] = ADD_OP;
        }
        else
        {
            res[i] = GET_OP;
        }
    }
    return res;
}

void run_threads(int numthreads, ptarg_t args[], int lockstrat)
{
    pthread_t *threads;
    glinkedlist_t *g;
    int i;

    threads = (pthread_t *)malloc(sizeof(pthread_t) * numthreads);
    g = glinkedlist_new(sizeof(int), sizeof(int), lockstrat);
    for (i = 0; i < numthreads; ++i)
    {
        args[i].g = g;
        pthread_create(&threads[i], NULL, run_thread, (void *)&args[i]);
    }
    for (i = 0; i < numthreads; ++i)
    {
        pthread_join(threads[i], NULL);
    }
}

void *run_thread(void *arg)
{
    int i, key, val;

    val = 0;
    ptarg_t *a = (ptarg_t *)arg;
    for (i = 0; i < a->numops; ++i)
    {
        key = rand() % a->numkeys;
        if (a->ops[i] == ADD_OP)
        {
            printd("ADD key %d\n", key);
            glinkedlist_add(a->g, &key, &val);
        }
        else
        {
            printd("GET key %d\n", key);
            glinkedlist_get(a->g, &key);
        }
    }
    return NULL;
}
