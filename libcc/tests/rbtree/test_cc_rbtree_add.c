#include "cc_rbtree.h"
#include <assert.h>

int new_cmp(void *key1, void *key2);
void add_add(cc_rbtree *t, char *key, int val);
void add_assert_node(cc_rbtree_node *n, void *key, int val, int color);
void add_assert_leaf(cc_rbtree_node *n);

int test_cc_rbtree_add() {
  cc_rbtree *t = cc_rbtree_new(sizeof(char **), sizeof(int), new_cmp);
  assert(cc_rbtree_size(t) == 0);

  add_add(t, "S", 0);
  add_assert_node(t->root, "S", 0, 1);
  add_assert_leaf(t->root);
  assert(cc_rbtree_size(t) == 1);

  add_add(t, "E", 1);
  add_assert_node(t->root, "S", 0, 1);
  assert(t->root->right == NULL);
  add_assert_node(t->root->left, "E", 1, 0);
  add_assert_leaf(t->root->left);
  assert(cc_rbtree_size(t) == 2);

  add_add(t, "A", 2);
  add_assert_node(t->root, "E", 1, 1);
  add_assert_node(t->root->left, "A", 2, 1);
  add_assert_leaf(t->root->left);
  add_assert_node(t->root->right, "S", 0, 1);
  add_assert_leaf(t->root->right);
  assert(cc_rbtree_size(t) == 3);

  add_add(t, "R", 3);
  add_assert_node(t->root, "E", 1, 1);
  add_assert_node(t->root->left, "A", 2, 1);
  add_assert_leaf(t->root->left);
  add_assert_node(t->root->right, "S", 0, 1);
  assert(t->root->right->right == NULL);
  add_assert_node(t->root->right->left, "R", 3, 0);
  add_assert_leaf(t->root->right->left);
  assert(cc_rbtree_size(t) == 4);

  add_add(t, "C", 4);
  add_assert_node(t->root, "E", 1, 1);
  add_assert_node(t->root->left, "C", 4, 1);
  assert(t->root->left->right == NULL);
  add_assert_node(t->root->left->left, "A", 2, 0);
  add_assert_leaf(t->root->left->left);
  add_assert_node(t->root->right, "S", 0, 1);
  assert(t->root->right->right == NULL);
  add_assert_node(t->root->right->left, "R", 3, 0);
  add_assert_leaf(t->root->right->left);
  assert(cc_rbtree_size(t) == 5);

  cc_rbtree_free(t);
  return 0;
}

void add_add(cc_rbtree *t, char *key, int val) { cc_rbtree_add(t, &key, &val); }

void add_assert_node(cc_rbtree_node *n, void *key, int val, int color) {
  assert(new_cmp(n->key, &key) == 0);
  assert(*(int *)n->val == val);
  assert(n->color == color);
}

void add_assert_leaf(cc_rbtree_node *n) {
  assert(n->left == NULL);
  assert(n->right == NULL);
}
