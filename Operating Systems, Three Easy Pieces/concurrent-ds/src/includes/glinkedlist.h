#ifndef GLINKEDLIST_H
#define GLINKEDLIST_H

#include <pthread.h>

#define GLINKEDLIST_LOCKSTRAT_GLOBAL 1
#define GLINKEDLIST_LOCKSTRAT_HOH 2

typedef struct node_s
{
    pthread_mutex_t lock;
    void *key;
    void *val;
    struct node_s *next;
} node_t;

typedef struct
{
    pthread_mutex_t lock;
    int lockstrat;
    int keysize;
    int valsize;
    int n;
    node_t *head;
} glinkedlist_t;

glinkedlist_t *glinkedlist_new(int keysize, int valsize, int lockstrat);

void glinkedlist_add(glinkedlist_t *g, void *key, void *val);

void *glinkedlist_get(glinkedlist_t *g, void *key);

#endif
