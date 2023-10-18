#ifndef CLIBC_ARRAY
#define CLIBC_ARRAY
#include <stddef.h>

typedef struct {
  size_t cap;
  size_t elem_sz;
  size_t size;
  void *data;
} clibc_array;

clibc_array *clibc_array_make(size_t elem_sz);

#endif
