#ifndef RWLINE_H
#define RWLINE_H

#include <pthread.h>

#include "glib.h"

typedef struct _rwline_t
{
    pthread_mutex_t mutex;
    GQueue q;
} rwline_t;

void rwline_init(rwline_t *rw);
void rwline_acquire_readlock(rwline_t *rw);
void rwline_release_readlock(rwline_t *rw);
void rwline_acquire_writelock(rwline_t *rw);
void rwline_release_writelock(rwline_t *rw);

#endif
