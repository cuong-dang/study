#include "cc_pqueue.h"
#include <assert.h>

int char_cmp(void *c1, void *c2);

int test_cc_pqueue_add_rm() {
  cc_pqueue *pq;
  char c;

  pq = cc_pqueue_new(sizeof(int), char_cmp);
  c = 'S';
  cc_pqueue_add(pq, &c);
  c = 'E';
  cc_pqueue_add(pq, &c);
  c = 'A';
  cc_pqueue_add(pq, &c);
  c = 'R';
  cc_pqueue_add(pq, &c);
  c = 'C';
  cc_pqueue_add(pq, &c);
  c = 'H';
  cc_pqueue_add(pq, &c);
  c = 'E';
  cc_pqueue_add(pq, &c);
  c = 'X';
  cc_pqueue_add(pq, &c);

  cc_pqueue_rm(pq, &c);
  assert(c == 'X');
  cc_pqueue_rm(pq, &c);
  assert(c == 'S');
  cc_pqueue_rm(pq, &c);
  assert(c == 'R');
  cc_pqueue_rm(pq, &c);
  assert(c == 'H');
  cc_pqueue_rm(pq, &c);
  assert(c == 'E');
  cc_pqueue_rm(pq, &c);
  assert(c == 'E');
  cc_pqueue_rm(pq, &c);
  assert(c == 'C');
  cc_pqueue_rm(pq, &c);
  assert(c == 'A');

  cc_pqueue_free(pq);
  return 0;
}

int char_cmp(void *c1, void *c2) { return *(char *)c1 - *(char *)c2; }
