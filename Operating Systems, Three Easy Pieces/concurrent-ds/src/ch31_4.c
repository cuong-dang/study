#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "zem.h"

//
// Your code goes in the structure and functions below
//

typedef struct __rwlock_t
{
    Zem_t mutex;
    Zem_t wlock;
    int num_readers;
} rwlock_t;

void rwlock_init(rwlock_t *rw)
{
    Zem_init(&rw->mutex, 1);
    Zem_init(&rw->wlock, 1);
    rw->num_readers = 0;
}

void rwlock_acquire_readlock(rwlock_t *rw)
{
    Zem_wait(&rw->mutex);
    if (rw->num_readers == 0)
    {
        Zem_wait(&rw->wlock);
    }
    ++rw->num_readers;
    Zem_post(&rw->mutex);
}

void rwlock_release_readlock(rwlock_t *rw)
{
    Zem_wait(&rw->mutex);
    --rw->num_readers;
    if (rw->num_readers == 0)
    {
        Zem_post(&rw->wlock);
    }
    Zem_post(&rw->mutex);
}

void rwlock_acquire_writelock(rwlock_t *rw)
{
    Zem_wait(&rw->wlock);
}

void rwlock_release_writelock(rwlock_t *rw)
{
    Zem_post(&rw->wlock);
}

//
// Don't change the code below (just use it!)
//

int loops;
int value = 0;

rwlock_t lock;

void *reader(void *arg)
{
    int i;
    for (i = 0; i < loops; i++)
    {
        rwlock_acquire_readlock(&lock);
        printf("read %d\n", value);
        rwlock_release_readlock(&lock);
    }
    return NULL;
}

void *writer(void *arg)
{
    int i;
    for (i = 0; i < loops; i++)
    {
        rwlock_acquire_writelock(&lock);
        value++;
        printf("write %d\n", value);
        rwlock_release_writelock(&lock);
    }
    return NULL;
}

int main(int argc, char *argv[])
{
    assert(argc == 4);
    int num_readers = atoi(argv[1]);
    int num_writers = atoi(argv[2]);
    loops = atoi(argv[3]);

    pthread_t pr[num_readers], pw[num_writers];

    rwlock_init(&lock);

    printf("begin\n");

    int i;
    for (i = 0; i < num_readers; i++)
        pthread_create(&pr[i], NULL, reader, NULL);
    for (i = 0; i < num_writers; i++)
        pthread_create(&pw[i], NULL, writer, NULL);

    for (i = 0; i < num_readers; i++)
        pthread_join(pr[i], NULL);
    for (i = 0; i < num_writers; i++)
        pthread_join(pw[i], NULL);

    printf("end: value %d\n", value);

    return 0;
}
