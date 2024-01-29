#include "cc_linkedlist.h"
#include <stdlib.h>
#include <string.h>

cc_linkedlist *cc_linkedlist_new(size_t elem_sz) {
  cc_linkedlist *ll;

  ll = malloc(sizeof(cc_linkedlist));
  ll->elem_sz = elem_sz;
  ll->head = NULL;
  return ll;
}

void cc_linkedlist_add(cc_linkedlist *ll, void *elem) {
  cc_linkedlist_node *n;

  n = malloc(sizeof(cc_linkedlist_node));
  n->data = malloc(ll->elem_sz);
  memcpy(n->data, elem, ll->elem_sz);
  n->next = ll->head;
  ll->head = n;
}

void cc_linkedlist_free(cc_linkedlist *ll) {
  cc_linkedlist_node *x, *next;

  for (x = ll->head; x != NULL; x = next) {
    next = x->next;
    free(x->data);
    free(x);
  }
  free(ll);
}
