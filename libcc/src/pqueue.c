#include "cc_array.h"
#include "cc_pqueue.h"
#include <stdlib.h>
#include <string.h>

void swim(cc_pqueue *pq, int i);
void sink(cc_pqueue *pq, int i);

cc_pqueue *cc_pqueue_new(size_t elem_sz, cmpfn *cmpfn) {
  cc_pqueue *pq;
  void *null;

  pq = malloc(sizeof(cc_pqueue));
  pq->pqa = cc_array_new(elem_sz);
  null = malloc(elem_sz);
  cc_array_add(pq->pqa, &null);
  pq->cmpfn = cmpfn;
  free(null);
  return pq;
}

size_t cc_pqueue_size(cc_pqueue *pq) { return pq->pqa->size - 1; }

void cc_pqueue_add(cc_pqueue *pq, void *elem) {
  cc_array_add(pq->pqa, elem);
  swim(pq, pq->pqa->size - 1);
}

void cc_pqueue_rm(cc_pqueue *pq, void *out) {
  memcpy(out, cc_array_get(pq->pqa, 1), pq->pqa->elem_sz);
  cc_array_swap(pq->pqa, 1, pq->pqa->size - 1);
  cc_array_rm(pq->pqa, pq->pqa->size - 1);
  sink(pq, 1);
}

void cc_pqueue_free(cc_pqueue *pq) {
  cc_array_free(pq->pqa);
  free(pq);
}

void swim(cc_pqueue *pq, int i) {
  while (i > 1 && pq->cmpfn(cc_array_get(pq->pqa, i / 2),
                            cc_array_get(pq->pqa, i)) < 0) {
    cc_array_swap(pq->pqa, i / 2, i);
    i = i / 2;
  }
}

void sink(cc_pqueue *pq, int i) {
  int n, j;

  n = pq->pqa->size - 1;
  while (2 * i <= n) {
    j = 2 * i;
    if (j < n &&
        pq->cmpfn(cc_array_get(pq->pqa, j), cc_array_get(pq->pqa, j + 1)) < 0) {
      j++;
    }
    if (!(pq->cmpfn(cc_array_get(pq->pqa, i), cc_array_get(pq->pqa, j)) < 0)) {
      break;
    }
    cc_array_swap(pq->pqa, i, j);
    i = j;
  }
}
