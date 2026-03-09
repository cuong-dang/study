#include "zem.h"

void Zem_init(Zem_t *z, int value)
{
    z->value = value;
    z->posted = 0;
    pthread_cond_init(&z->cond, NULL);
    pthread_mutex_init(&z->lock, NULL);
}

void Zem_wait(Zem_t *z)
{
    int am_waiting = 0;

    pthread_mutex_lock(&z->lock);
    --z->value;
    while (z->value < 0 && !(z->posted && am_waiting))
    {
        am_waiting = 1;
        pthread_cond_wait(&z->cond, &z->lock);
    }
    z->posted = 0;
    pthread_mutex_unlock(&z->lock);
}

void Zem_post(Zem_t *z)
{
    pthread_mutex_lock(&z->lock);
    ++z->value;
    z->posted = 1;
    pthread_cond_signal(&z->cond);
    pthread_mutex_unlock(&z->lock);
}
