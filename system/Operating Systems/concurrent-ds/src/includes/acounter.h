#ifndef CONCURRENT_DS_ACOUNTER_H
#define CONCURRENT_DS_ACOUNTER_H

#include <pthread.h>

#include "ctypes.h"

typedef struct {
    int n_threads;
    int g_value;
    mutex_t g_lock;
    int u_threshold;
    int *l_values;
    mutex_t *l_locks;
} acounter_t;

void init(acounter_t *c, int n_threads, int u_threshold);

void inc(acounter_t *c, int thread_id);

int get(acounter_t *c);

int get_exact(acounter_t *c);

void destroy(acounter_t *c);

#endif //CONCURRENT_DS_ACOUNTER_H
