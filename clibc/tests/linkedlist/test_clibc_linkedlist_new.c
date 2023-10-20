#include "clibc_linkedlist.h"
#include <assert.h>

int test_clibc_linkedlist_new() {
  clibc_linkedlist *ll = clibc_linkedlist_new(sizeof(int));

  assert(ll->elem_sz == sizeof(int));
  assert(ll->head == NULL);
  clibc_linkedlist_free(ll);
  return 0;
}
