#ifndef CC_SET
#define CC_SET
#include "cc_rbtree.h"

typedef struct {
  cc_rbtree *set;
} cc_set;

cc_set *cc_set_new(size_t elem_sz, cmpfn *cmpfn);
void cc_set_add(cc_set *s, void *elem);
int cc_set_contains(cc_set *s, void *elem);
void cc_set_free(cc_set *s);

#endif
