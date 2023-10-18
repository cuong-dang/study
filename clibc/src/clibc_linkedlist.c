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

void clibc_linkedlist_free(clibc_linkedlist *ll) {
  clibc_node *x, *next;

  for (x = ll->head; x != NULL; x = next) {
    next = x->next;
    free(x->data);
    free(x);
  }
  free(ll);
}
