#include "cc_rbtree.h"
#include <assert.h>

int new_cmp(void *key1, void *key2);
void add_add(cc_rbtree *t, char *key, int val);
int get_get(cc_rbtree *t, char *key);

int test_cc_rbtree_get() {
  cc_rbtree *t = cc_rbtree_new(sizeof(char **), sizeof(int), new_cmp);

  add_add(t, "S", 0);
  add_add(t, "E", 1);
  add_add(t, "A", 2);
  add_add(t, "R", 3);
  add_add(t, "C", 4);
  add_add(t, "H", 5);

  assert(get_get(t, "S") == 0);
  assert(get_get(t, "E") == 1);
  assert(get_get(t, "A") == 2);
  assert(get_get(t, "R") == 3);
  assert(get_get(t, "C") == 4);
  assert(get_get(t, "H") == 5);

  cc_rbtree_free(t);
  return 0;
}

int get_get(cc_rbtree *t, char *key) { return *(int *)cc_rbtree_get(t, &key); }
