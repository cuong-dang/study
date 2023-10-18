#include "clibc_array.h"
#include <assert.h>

int test_clibc_array_rm() {
  clibc_array *a = clibc_array_new(sizeof(int));
  int i = 0, oldcap;

  clibc_array_add(a, &i);
  i = 1;
  clibc_array_add(a, &i);
  clibc_array_rm(a, 0);
  assert(a->size == 1);
  assert(*(int *)clibc_array_get(a, 0) == 1);
  clibc_array_rm(a, 0);
  assert(a->size == 0);

  oldcap = a->cap;
  for (i = 0; i < 2 * oldcap; i++) {
    clibc_array_add(a, &i);
  }
  assert(a->cap == 2 * oldcap);
  for (i = 0; i < oldcap; i++) {
    clibc_array_rm(a, i);
  }
  assert(a->cap == oldcap);
  return 0;
}
