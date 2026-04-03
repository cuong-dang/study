#include "rwline.h"

typedef struct _rwline_block_t
{
    pthread_cond_t cond;
    int is_readersblock;
    int num_readers;
} rwline_block_t;

rwline_block_t *new_block(int is_readersblock);

void rwline_init(rwline_t *rw)
{
    pthread_mutex_init(&rw->mutex, NULL);
    g_queue_init(&rw->q);
}

void rwline_acquire_readlock(rwline_t *rw)
{
    rwline_block_t *b, *t;

    pthread_mutex_lock(&rw->mutex);
    /* Case 1: Empty queue */
    if (g_queue_is_empty(&rw->q))
    {
        b = new_block(1);
        g_queue_push_tail(&rw->q, b);
    }
    else
    /* Case 2: Non-empty queue */
    {
        t = g_queue_peek_tail(&rw->q);
        /* Case 2.1: If tail is reader block */
        if (t->is_readersblock)
        {
            /* Case 2.1.1: If there is only 1 block */
            if (g_queue_get_length(&rw->q) == 1)
            {
                ++t->num_readers;
            }
            /* Case 2.1.1: If there are more than 1 blocks */
            else
            {
                ++t->num_readers;
                pthread_cond_wait(&t->cond, &rw->mutex);
            }
        }
        /* Case 2.2: If tail is writer block */
        else
        {
            b = new_block(1);
            g_queue_push_tail(&rw->q, b);
            pthread_cond_wait(&b->cond, &rw->mutex);
        }
    }
    pthread_mutex_unlock(&rw->mutex);
}

void rwline_release_readlock(rwline_t *rw)
{
    rwline_block_t *h;

    pthread_mutex_lock(&rw->mutex);
    h = g_queue_peek_head(&rw->q);
    --h->num_readers;
    if (h->num_readers == 0)
    {
        g_queue_pop_head(&rw->q);
        h = g_queue_peek_head(&rw->q);
        if (h)
        {
            pthread_cond_broadcast(&h->cond);
        }
    }
    pthread_mutex_unlock(&rw->mutex);
}

void rwline_acquire_writelock(rwline_t *rw)
{
    rwline_block_t *b, *t;

    pthread_mutex_lock(&rw->mutex);
    if (g_queue_is_empty(&rw->q))
    {
        b = new_block(0);
        g_queue_push_tail(&rw->q, b);
    }
    else
    {
        b = new_block(0);
        g_queue_push_tail(&rw->q, b);
        pthread_cond_wait(&b->cond, &rw->mutex);
    }
    pthread_mutex_unlock(&rw->mutex);
}

void rwline_release_writelock(rwline_t *rw)
{
    rwline_block_t *h;

    pthread_mutex_lock(&rw->mutex);
    g_queue_pop_head(&rw->q);
    h = g_queue_peek_head(&rw->q);
    if (h)
    {
        pthread_cond_broadcast(&h->cond);
    }
    pthread_mutex_unlock(&rw->mutex);
}

rwline_block_t *new_block(int is_readersblock)
{
    rwline_block_t *b;

    b = (rwline_block_t *)malloc(sizeof(rwline_block_t));
    pthread_cond_init(&b->cond, NULL);
    b->is_readersblock = is_readersblock;
    b->num_readers = is_readersblock ? 1 : 0;
    return b;
}
