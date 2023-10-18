#include "clibc_array.h"
#include <stdlib.h>

#define DEFAULT_CAP 128

clibc_array *clibc_array_make(size_t elem_sz) {
  clibc_array *rv;

  if ((rv = (clibc_array *)malloc(sizeof(clibc_array))) == NULL) {
    return NULL;
  }
  rv->cap = DEFAULT_CAP;
  rv->elem_sz = elem_sz;
  rv->size = 0;
  if ((rv->data = (void *)malloc(sizeof(rv->cap * elem_sz))) == NULL) {
    free(rv);
    return NULL;
  }
  return rv;
}
