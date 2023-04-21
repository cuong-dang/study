#include <stdlib.h>
#include <string.h>

#include "glinkedlist.h"

/* NOT CHECKING FOR SYSCALLS BECAUSE THIS IS A TOY PROGRAM */

glinkedlist_t *glinkedlist_new(int keysize, int valsize, int lockstrat)
{
    glinkedlist_t *g;

    g = (glinkedlist_t *)malloc(sizeof(glinkedlist_t));
    g->head = NULL;
    g->keysize = keysize;
    g->valsize = valsize;
    g->n = 0;
    g->lockstrat = lockstrat;
    pthread_mutex_init(&g->lock, NULL);
    return g;
}

void glinkedlist_add(glinkedlist_t *g, void *key, void *val)
{
    node_t *new;

    new = (node_t *)malloc(sizeof(node_t));
    new->key = malloc(sizeof(g->keysize));
    new->val = malloc(sizeof(g->valsize));
    memcpy(new->key, key, g->keysize);
    memcpy(new->val, val, g->valsize);
    if (g->lockstrat == GLINKEDLIST_LOCKSTRAT_HOH)
    {
        pthread_mutex_init(&new->lock, NULL);
    }

    pthread_mutex_lock(&g->lock);
    new->next = g->head;
    g->head = new;
    ++g->n;
    pthread_mutex_unlock(&g->lock);
}

void *glinkedlist_get(glinkedlist_t *g, void *key)
{
    node_t *x;

    pthread_mutex_lock(&g->lock);
    x = g->head;
    if (g->lockstrat == GLINKEDLIST_LOCKSTRAT_HOH)
    {
        if (x != NULL)
        {
            pthread_mutex_lock(&x->lock);
        }
        pthread_mutex_unlock(&g->lock);
    }
    for (; x != NULL; x = x->next)
    {
        if (memcmp(x->key, key, g->keysize) == 0)
        {
            if (g->lockstrat == GLINKEDLIST_LOCKSTRAT_GLOBAL)
            {
                pthread_mutex_unlock(&g->lock);
            }
            else
            {
                pthread_mutex_unlock(&x->lock);
            }
            return x->val;
        }
        if (g->lockstrat == GLINKEDLIST_LOCKSTRAT_HOH)
        {
            if (x->next != NULL)
            {
                pthread_mutex_lock(&x->next->lock);
            }
            pthread_mutex_unlock(&x->lock);
        }
    }
    if (g->lockstrat == GLINKEDLIST_LOCKSTRAT_GLOBAL)
    {
        pthread_mutex_unlock(&g->lock);
    }
    return NULL;
}
