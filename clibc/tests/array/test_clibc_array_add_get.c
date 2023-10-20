#include "clibc_array.h"
#include <assert.h>

int test_clibc_array_add_get() {
  clibc_array *a = clibc_array_new(sizeof(int));
  int i;
  size_t oldcap;

  /* Insert up to capacity */
  for (i = 0; i < a->cap; ++i) {
    clibc_array_add(a, &i);
    assert(a->size == i + 1);
    assert(*(int *)clibc_array_get(a, i) == i);
  }

  /* Grow */
  assert(a->size == a->cap);
  oldcap = a->cap;
  clibc_array_add(a, &i);
  assert(a->cap == 2 * oldcap);
  clibc_array_free(a);
  return 0;
}
