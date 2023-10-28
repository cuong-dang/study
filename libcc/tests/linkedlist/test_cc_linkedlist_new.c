#include "cc_linkedlist.h"
#include <assert.h>

int test_cc_linkedlist_new() {
  cc_linkedlist *ll = cc_linkedlist_new(sizeof(int));

  assert(ll->elem_sz == sizeof(int));
  assert(ll->head == NULL);
  cc_linkedlist_free(ll);
  return 0;
}
