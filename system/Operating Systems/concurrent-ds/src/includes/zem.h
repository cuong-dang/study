#ifndef ZEM_H
#define ZEM_H

#include <pthread.h>

typedef struct __Zem_t
{
    int value;
    int posted;
    pthread_cond_t cond;
    pthread_mutex_t lock;
} Zem_t;

void Zem_init(Zem_t *z, int value);
void Zem_wait(Zem_t *z);
void Zem_post(Zem_t *z);

#endif
