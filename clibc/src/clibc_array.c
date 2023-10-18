#include "clibc_array.h"
#include <stdlib.h>
#include <string.h>

#define DEFAULT_CAP 32

void grow(clibc_array *a);

clibc_array *clibc_array_new(size_t elem_sz) {
  clibc_array *a;

  a = malloc(sizeof(clibc_array));
  a->cap = DEFAULT_CAP;
  a->elem_sz = elem_sz;
  a->size = 0;
  a->data = malloc(a->cap * a->elem_sz);
  return a;
}

void clibc_array_add(clibc_array *a, void *elem) {
  if (a->size == a->cap) {
    grow(a);
  }
  memcpy((char *)a->data + a->size * a->elem_sz, elem, a->elem_sz);
  a->size++;
}

void *clibc_array_get(clibc_array *a, size_t i) {
  return (char *)a->data + i * a->elem_sz;
}

void grow(clibc_array *a) {
  a->cap *= 2;
  a->data = reallocarray(a->data, a->cap, a->elem_sz);
}
