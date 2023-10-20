#include "clibc_linkedlist.h"
#include <stdlib.h>
#include <string.h>

clibc_linkedlist *clibc_linkedlist_new(size_t elem_sz) {
  clibc_linkedlist *ll;

  ll = malloc(sizeof(clibc_linkedlist));
  ll->elem_sz = elem_sz;
  ll->head = NULL;
  return ll;
}

void clibc_linkedlist_add(clibc_linkedlist *ll, void *elem) {
  clibc_linkedlist_node *n;

  n = malloc(sizeof(clibc_linkedlist_node));
  n->data = malloc(ll->elem_sz);
  memcpy(n->data, elem, ll->elem_sz);
  n->next = ll->head;
  ll->head = n;
}

void clibc_linkedlist_free(clibc_linkedlist *ll) {
  clibc_linkedlist_node *x, *next;

  for (x = ll->head; x != NULL; x = next) {
    next = x->next;
    free(x->data);
    free(x);
  }
  free(ll);
}
