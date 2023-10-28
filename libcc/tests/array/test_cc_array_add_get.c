#include "cc_array.h"
#include <assert.h>

int test_cc_array_add_get() {
  cc_array *a = cc_array_new(sizeof(int));
  int i;
  size_t oldcap;

  /* Insert up to capacity */
  for (i = 0; i < a->cap; ++i) {
    cc_array_add(a, &i);
    assert(a->size == i + 1);
    assert(*(int *)cc_array_get(a, i) == i);
  }

  /* Grow */
  assert(a->size == a->cap);
  oldcap = a->cap;
  cc_array_add(a, &i);
  assert(a->cap == 2 * oldcap);
  cc_array_free(a);
  return 0;
}
