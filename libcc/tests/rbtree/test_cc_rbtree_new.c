#include "cc_rbtree.h"
#include <assert.h>
#include <string.h>

int new_cmp(void *key1, void *key2) {
  return strcmp(*(char **)key1, *(char **)key2);
}

int test_cc_rbtree_new() {
  cc_rbtree *t = cc_rbtree_new(sizeof(char **), sizeof(int), new_cmp);

  assert(t->key_sz == sizeof(char **));
  assert(t->val_sz == sizeof(int));
  assert(t->cmpfn == new_cmp);
  assert(t->root == NULL);
  cc_rbtree_free(t);
  return 0;
}
