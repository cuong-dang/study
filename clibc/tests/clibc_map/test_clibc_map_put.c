#include "clibc_map.h"
#include <assert.h>
#include <string.h>

int cmp(void *key1, void *key2);
void assert_node(clibc_map_node *n, void *key, int val,
                 clibc_map_node_color color);
void assert_leaf(clibc_map_node *n);

int test_clibc_map_put() {
  clibc_map *m = clibc_map_new(sizeof(char *), sizeof(int), cmp);
  int v = 0;

  clibc_map_put(m, "S", &v);
  v++;
  assert_node(m->root, "S", 0, 1);
  assert_leaf(m->root);

  clibc_map_put(m, "E", &v);
  assert_node(m->root, "S", 0, 1);
  assert(m->root->right == NULL);
  assert_node(m->root->left, "E", 1, 0);
  assert_leaf(m->root->left);
  v++;

  clibc_map_put(m, "A", &v);
  v++;
  assert_node(m->root, "E", 1, 1);
  assert_node(m->root->left, "A", 2, 1);
  assert_leaf(m->root->left);
  assert_node(m->root->right, "S", 0, 1);
  assert_leaf(m->root->right);

  clibc_map_put(m, "R", &v);
  v++;
  assert_node(m->root, "E", 1, 1);
  assert_node(m->root->left, "A", 2, 1);
  assert_leaf(m->root->left);
  assert_node(m->root->right, "S", 0, 1);
  assert(m->root->right->right == NULL);
  assert_node(m->root->right->left, "R", 3, 0);
  assert_leaf(m->root->right->left);

  clibc_map_put(m, "C", &v);
  assert_node(m->root, "E", 1, 1);
  assert_node(m->root->left, "C", 4, 1);
  assert(m->root->left->right == NULL);
  assert_node(m->root->left->left, "A", 2, 0);
  assert_leaf(m->root->left->left);
  assert_node(m->root->right, "S", 0, 1);
  assert(m->root->right->right == NULL);
  assert_node(m->root->right->left, "R", 3, 0);
  assert_leaf(m->root->right->left);

  return 0;
}

void assert_node(clibc_map_node *n, void *key, int val,
                 clibc_map_node_color color) {
  assert(cmp(n->key, key) == 0);
  assert(*(int *)n->val == val);
  assert(n->color == color);
}

void assert_leaf(clibc_map_node *n) {
  assert(n->left == NULL);
  assert(n->right == NULL);
}
