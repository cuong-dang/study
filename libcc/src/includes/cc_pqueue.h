#ifndef CC_PQUEUE
#define CC_PQUEUE
#include "cc_rbtree.h"

typedef struct {
  cc_array *pqa;
  cmpfn *cmpfn;
} cc_pqueue;

cc_pqueue *cc_pqueue_new(size_t elem_sz, cmpfn *cmpfn);
size_t cc_pqueue_size(cc_pqueue *pq);
void cc_pqueue_add(cc_pqueue *pq, void *elem);
void cc_pqueue_rm(cc_pqueue *pq, void *out);
void cc_pqueue_free(cc_pqueue *pq);

#endif
