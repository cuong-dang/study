#include "clibc_array.h"
#include <assert.h>

int test_clibc_array_new() {
  clibc_array *a = clibc_array_new(sizeof(int));

  assert(a->cap == 32);
  assert(a->elem_sz == sizeof(int));
  assert(a->size == 0);
  return 0;
}
