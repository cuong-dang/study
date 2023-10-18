#include "clibc_array.h"
#include <assert.h>

int test_clibc_array_make() {
  clibc_array *a = clibc_array_make(4);

  assert(a->cap == 128);
  assert(a->elem_sz == 4);
  assert(a->size == 0);
  return 0;
}
