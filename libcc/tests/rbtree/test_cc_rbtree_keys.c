#include "cc_array.h"
#include "cc_rbtree.h"
#include <assert.h>
#include <string.h>

int new_cmp(void *key1, void *key2);
void add_add(cc_rbtree *t, char *key, int val);
void keys_assert_key(cc_array *keys, int i, char *k);

int test_cc_rbtree_keys() {
  cc_rbtree *t = cc_rbtree_new(sizeof(char **), sizeof(int), new_cmp);
  cc_array *keys;

  add_add(t, "S", 0);
  add_add(t, "E", 1);
  add_add(t, "A", 2);
  add_add(t, "R", 3);
  add_add(t, "C", 4);
  add_add(t, "H", 5);

  keys = cc_array_new(sizeof(char *));
  cc_rbtree_keys(t, keys);
  assert(keys->size == 6);
  keys_assert_key(keys, 0, "A");
  keys_assert_key(keys, 1, "C");
  keys_assert_key(keys, 2, "E");
  keys_assert_key(keys, 3, "H");
  keys_assert_key(keys, 4, "R");
  keys_assert_key(keys, 5, "S");

  cc_array_free(keys);
  cc_rbtree_free(t);
  return 0;
}

void keys_assert_key(cc_array *keys, int i, char *k) {
  assert(strcmp(*(char **)cc_array_get(keys, i), k) == 0);
}
