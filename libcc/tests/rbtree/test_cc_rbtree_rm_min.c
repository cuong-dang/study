#include "cc_array.h"
#include "cc_rbtree.h"
#include <assert.h>
#include <string.h>

int new_cmp(void *key1, void *key2);
void add_add(cc_rbtree *t, char *key, int val);
int get_get(cc_rbtree *t, char *key);
void keys_assert_key(cc_array *keys, int i, char *k);

int test_cc_rbtree_rm_min() {
  cc_rbtree *t = cc_rbtree_new(sizeof(char **), sizeof(int), new_cmp);
  int outval;
  char *outkey;
  cc_array *keys;

  add_add(t, "S", 0);
  add_add(t, "E", 1);
  add_add(t, "A", 2);
  add_add(t, "R", 3);
  add_add(t, "C", 4);
  add_add(t, "H", 5);
  assert(cc_rbtree_size(t) == 6);

  keys = cc_array_new(sizeof(char **));
  cc_rbtree_rm_min(t, &outkey, &outval);
  assert(strcmp(outkey, "A") == 0);
  assert(outval == 2);
  cc_rbtree_keys(t, keys);
  assert(keys->size == 5);
  keys_assert_key(keys, 0, "C");
  keys_assert_key(keys, 1, "E");
  keys_assert_key(keys, 2, "H");
  keys_assert_key(keys, 3, "R");
  keys_assert_key(keys, 4, "S");
  assert(cc_rbtree_size(t) == 5);

  cc_rbtree_rm_min(t, &outkey, &outval);
  assert(strcmp(outkey, "C") == 0);
  assert(outval == 4);
  cc_rbtree_keys(t, keys);
  assert(keys->size == 4);
  keys_assert_key(keys, 0, "E");
  keys_assert_key(keys, 1, "H");
  keys_assert_key(keys, 2, "R");
  keys_assert_key(keys, 3, "S");
  assert(cc_rbtree_size(t) == 4);

  cc_rbtree_rm_min(t, &outkey, &outval);
  assert(strcmp(outkey, "E") == 0);
  assert(outval == 1);
  cc_rbtree_keys(t, keys);
  assert(keys->size == 3);
  keys_assert_key(keys, 0, "H");
  keys_assert_key(keys, 1, "R");
  keys_assert_key(keys, 2, "S");
  assert(cc_rbtree_size(t) == 3);

  cc_rbtree_rm_min(t, &outkey, &outval);
  assert(strcmp(outkey, "H") == 0);
  assert(outval == 5);
  cc_rbtree_keys(t, keys);
  assert(keys->size == 2);
  keys_assert_key(keys, 0, "R");
  keys_assert_key(keys, 1, "S");
  assert(cc_rbtree_size(t) == 2);

  cc_rbtree_rm_min(t, &outkey, &outval);
  assert(strcmp(outkey, "R") == 0);
  assert(outval == 3);
  cc_rbtree_keys(t, keys);
  assert(keys->size == 1);
  keys_assert_key(keys, 0, "S");
  assert(cc_rbtree_size(t) == 1);

  cc_rbtree_rm_min(t, &outkey, &outval);
  assert(strcmp(outkey, "S") == 0);
  assert(outval == 0);
  cc_rbtree_keys(t, keys);
  assert(keys->size == 0);
  assert(cc_rbtree_size(t) == 0);

  cc_array_free(keys);
  cc_rbtree_free(t);
  return 0;
}
