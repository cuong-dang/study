#include "cc_array.h"
#include <assert.h>

int test_cc_array_rm() {
  cc_array *a = cc_array_new(sizeof(int));
  int i = 0, oldcap;

  cc_array_add(a, &i);
  i = 1;
  cc_array_add(a, &i);
  cc_array_rm(a, 0);
  assert(a->size == 1);
  assert(*(int *)cc_array_get(a, 0) == 1);
  cc_array_rm(a, 0);
  assert(a->size == 0);

  oldcap = a->cap;
  for (i = 0; i < 2 * oldcap; i++) {
    cc_array_add(a, &i);
  }
  assert(a->cap == 2 * oldcap);
  for (i = 0; i < oldcap; i++) {
    cc_array_rm(a, i);
  }
  assert(a->cap == oldcap);
  cc_array_free(a);
  return 0;
}
