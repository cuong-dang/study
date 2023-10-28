#include "cc_array.h"
#include <assert.h>

int test_cc_array_swap() {
  cc_array *a;
  int x, y;

  a = cc_array_new(sizeof(int));
  x = 0;
  y = 1;
  cc_array_add(a, &x);
  cc_array_add(a, &y);
  cc_array_swap(a, 0, 1);

  assert(*(int *)cc_array_get(a, 0) == y);
  assert(*(int *)cc_array_get(a, 1) == x);
  return 0;
}
