#include "cc_rbtree.h"
#include "cc_set.h"
#include <stdlib.h>

cc_set *cc_set_new(size_t elem_sz, cmpfn *cmpfn) {
  cc_set *s;

  s = malloc(sizeof(cc_set));
  s->set = cc_rbtree_new(elem_sz, sizeof(int), cmpfn);
  return s;
}

void cc_set_add(cc_set *s, void *elem) {
  int null;

  cc_rbtree_add(s->set, elem, &null);
}

int cc_set_contains(cc_set *s, void *elem) {
  return cc_rbtree_get(s->set, elem) != NULL;
}

void cc_set_free(cc_set *s) {
  cc_rbtree_free(s->set);
  free(s);
}
