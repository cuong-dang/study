#include "clibc_linkedlist.h"
#include <assert.h>

int test_clibc_linkedlist_add() {
  clibc_linkedlist *ll = clibc_linkedlist_new(sizeof(int));
  int i;

  for (i = 0; i < 4; i++) {
    clibc_linkedlist_add(ll, &i);
  }
  assert(*(int *)ll->head->data == 3);
  assert(*(int *)ll->head->next->data == 2);
  assert(*(int *)ll->head->next->next->data == 1);
  assert(*(int *)ll->head->next->next->next->data == 0);
  clibc_linkedlist_free(ll);
  return 0;
}
