#include "clibc_map.h"
#include <stdlib.h>
#include <string.h>

#define RED 0
#define BLACK 1

clibc_map_node *put(clibc_map *m, clibc_map_node *n, void *key, void *val);
clibc_map_node *new(clibc_map *m, void *key, void *val,
                    clibc_map_node_color color);
int is_red(clibc_map_node *n);
clibc_map_node *rotate_left(clibc_map_node *n);
clibc_map_node *rotate_right(clibc_map_node *n);
void flip_colors(clibc_map_node *n);
void *get(clibc_map *m, clibc_map_node *n, void *key);
void node_free(clibc_map_node *n);

clibc_map *clibc_map_new(size_t key_sz, size_t val_sz, cmp_fn *cmp_fn) {
  clibc_map *m = malloc(sizeof(clibc_map));

  m->key_sz = key_sz;
  m->val_sz = val_sz;
  m->cmp_fn = cmp_fn;
  m->root = NULL;
  return m;
}

void clibc_map_put(clibc_map *m, void *key, void *val) {
  m->root = put(m, m->root, key, val);
  m->root->color = BLACK;
}

void *clibc_map_get(clibc_map *m, void *key) { return get(m, m->root, key); }

void clibc_map_free(clibc_map *m) {
  node_free(m->root);
  free(m);
}

clibc_map_node *put(clibc_map *m, clibc_map_node *n, void *key, void *val) {
  int cmp;

  if (n == NULL) {
    return new (m, key, val, RED);
  }
  cmp = m->cmp_fn(key, n->key);
  if (cmp < 0) {
    n->left = put(m, n->left, key, val);
  } else if (cmp > 0) {
    n->right = put(m, n->right, key, val);
  } else {
    memcpy(n->val, val, m->val_sz);
  }

  if (is_red(n->right) && !is_red(n->left)) {
    n = rotate_left(n);
  }
  if (is_red(n->left) && is_red(n->left->left)) {
    n = rotate_right(n);
  }
  if (is_red(n->left) && is_red(n->right)) {
    flip_colors(n);
  }
  return n;
}

clibc_map_node *new(clibc_map *m, void *key, void *val,
                    clibc_map_node_color color) {
  clibc_map_node *n = malloc(sizeof(clibc_map_node));

  n->key = malloc(m->key_sz);
  memcpy(n->key, key, m->key_sz);
  n->val = malloc(m->val_sz);
  memcpy(n->val, val, m->val_sz);
  n->color = color;
  n->left = NULL;
  n->right = NULL;
  return n;
}

int is_red(clibc_map_node *n) {
  if (n == NULL) {
    return 0;
  }
  return n->color == RED;
}

clibc_map_node *rotate_left(clibc_map_node *n) {
  clibc_map_node *nn = n->right;

  n->right = nn->left;
  nn->left = n;
  nn->color = n->color;
  n->color = RED;
  return nn;
}

clibc_map_node *rotate_right(clibc_map_node *n) {
  clibc_map_node *nn = n->left;

  n->left = nn->right;
  nn->right = n;
  nn->color = n->color;
  n->color = RED;
  return nn;
}

void flip_colors(clibc_map_node *n) {
  n->color = RED;
  n->left->color = BLACK;
  n->right->color = BLACK;
}

void *get(clibc_map *m, clibc_map_node *n, void *key) {
  int cmp;

  if (n == NULL) {
    return NULL;
  }
  cmp = m->cmp_fn(key, n->key);
  if (cmp < 0) {
    return get(m, n->left, key);
  }
  if (cmp > 0) {
    return get(m, n->right, key);
  }
  return n->val;
}

void node_free(clibc_map_node *n) {
  if (n == NULL) {
    return;
  }
  free(n->key);
  free(n->val);
  node_free(n->left);
  node_free(n->right);
}
