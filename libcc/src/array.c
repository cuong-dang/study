#include "cc_array.h"
#include <stdlib.h>
#include <string.h>

#define DEFAULT_CAP 32

void grow(cc_array *a);
void shrink(cc_array *a);

cc_array *cc_array_new(size_t elem_sz) {
  cc_array *a;

  a = malloc(sizeof(cc_array));
  a->cap = DEFAULT_CAP;
  a->elem_sz = elem_sz;
  a->size = 0;
  a->data = malloc(a->cap * a->elem_sz);
  return a;
}

void cc_array_add(cc_array *a, void *elem) {
  if (a->size == a->cap) {
    grow(a);
  }
  memcpy((char *)a->data + a->size * a->elem_sz, elem, a->elem_sz);
  a->size++;
}

void *cc_array_get(cc_array *a, int i) {
  return (char *)a->data + i * a->elem_sz;
}

void cc_array_rm(cc_array *a, int i) {
  int j;

  for (j = i; j < a->size - 1; j++) {
    memcpy((char *)a->data + j * a->elem_sz,
           (char *)a->data + (j + 1) * a->elem_sz, a->elem_sz);
  }
  a->size--;
  if (a->size == a->cap / 2 && a->cap > DEFAULT_CAP) {
    shrink(a);
  }
}

void cc_array_swap(cc_array *a, int i, int j) {
  void *t;

  t = malloc(a->elem_sz);
  memcpy(t, (char *)a->data + i * a->elem_sz, a->elem_sz);
  memcpy((char *)a->data + i * a->elem_sz, (char *)a->data + j * a->elem_sz,
         a->elem_sz);
  memcpy((char *)a->data + j * a->elem_sz, t, a->elem_sz);
  free(t);
}

void cc_array_clear(cc_array *a) { a->size = 0; }

void cc_array_free(cc_array *a) {
  free(a->data);
  free(a);
}

void grow(cc_array *a) {
  a->cap *= 2;
  a->data = reallocarray(a->data, a->cap, a->elem_sz);
}

void shrink(cc_array *a) {
  a->cap /= 2;
  a->data = reallocarray(a->data, a->cap, a->elem_sz);
}
