#ifndef CC_ARRAY
#define CC_ARRAY
#include <stddef.h>

typedef struct {
  size_t cap;
  size_t elem_sz;
  size_t size;
  void *data;
} cc_array;

cc_array *cc_array_new(size_t elem_sz);
void cc_array_add(cc_array *a, void *elem);
void *cc_array_get(cc_array *a, int i);
void cc_array_swap(cc_array *a, int i, int j);
void cc_array_rm(cc_array *a, int i);
void cc_array_clear(cc_array *a);
void cc_array_free(cc_array *a);

#endif
