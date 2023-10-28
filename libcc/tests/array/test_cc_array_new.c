#include "cc_array.h"
#include <assert.h>

int test_cc_array_new() {
  cc_array *a = cc_array_new(sizeof(int));

  assert(a->cap == 32);
  assert(a->elem_sz == sizeof(int));
  assert(a->size == 0);
  cc_array_free(a);
  return 0;
}
