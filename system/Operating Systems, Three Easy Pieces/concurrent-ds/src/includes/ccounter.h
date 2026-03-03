#ifndef CCOUNTER_H
#define CCOUNTER_H

#include <pthread.h>

typedef struct __counter_t {
    int value;
    pthread_mutex_t lock;
} counter_t;

void init(counter_t *c);

void inc(counter_t *c);

int get(counter_t *c);

#endif //CCOUNTER_H
