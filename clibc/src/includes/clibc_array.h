#ifndef CLIBC_ARRAY
#define CLIBC_ARRAY
#include <stddef.h>

typedef struct {
  size_t cap;
  size_t elem_sz;
  size_t size;
  void *data;
} clibc_array;

clibc_array *clibc_array_new(size_t elem_sz);
void clibc_array_add(clibc_array *a, void *elem);
void *clibc_array_get(clibc_array *a, size_t i);
void clibc_array_rm(clibc_array *a, size_t i);
void clibc_array_clear(clibc_array *a);
void clibc_array_free(clibc_array *a);

#endif
