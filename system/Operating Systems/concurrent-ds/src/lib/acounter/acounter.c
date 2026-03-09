#include <stdlib.h>

#include "acounter.h"

void init(acounter_t *c, int n_threads, int u_threshold) {
    int i;

    c->n_threads = n_threads;
    c->g_value = 0;
    pthread_mutex_init(&c->g_lock, NULL);
    c->u_threshold = u_threshold;
    c->l_values = (int *) malloc(n_threads * sizeof(int));
    c->l_locks = (mutex_t *) malloc(n_threads * sizeof(mutex_t));
    for (i = 0; i < n_threads; ++i) {
        c->l_values[i] = 0;
        pthread_mutex_init(&c->l_locks[i], NULL);
    }
}

void inc(acounter_t *c, int thread_id) {
    int l_id;

    l_id = thread_id % c->n_threads;
    pthread_mutex_lock(&c->l_locks[l_id]);
    ++c->l_values[l_id];
    if (c->l_values[l_id] == c->u_threshold) {
        pthread_mutex_lock(&c->g_lock);
        c->g_value += c->l_values[l_id];
        pthread_mutex_unlock(&c->g_lock);
        c->l_values[l_id] = 0;
    }
    pthread_mutex_unlock(&c->l_locks[l_id]);
}

int get(acounter_t *c) {
    int r;

    pthread_mutex_lock(&c->g_lock);
    r = c->g_value;
    pthread_mutex_unlock(&c->g_lock);
    return r;
}

int get_exact(acounter_t *c) {
    int r;
    int i;

    r = 0;
    for (i = 0; i < c->n_threads; ++i) {
        pthread_mutex_lock(&c->l_locks[i]);
        r += c->l_values[i];
        pthread_mutex_unlock(&c->l_locks[i]);
    }
    pthread_mutex_lock(&c->g_lock);
    r += c->g_value;
    pthread_mutex_unlock(&c->g_lock);
    return r;
}

void destroy(acounter_t *c) {
    free(c->l_values);
    free(c->l_locks);
}
