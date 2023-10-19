#include "clibc_map.h"
#include <stdlib.h>
#include <string.h>

#define RED 0
#define BLACK 1

clibc_map_node *put(clibc_map *m, clibc_map_node *h, void *key, void *val);
clibc_map_node *new(clibc_map *m, void *key, void *val,
                    clibc_map_node_color color);
int is_red(clibc_map_node *h);
clibc_map_node *rotate_left(clibc_map_node *h);
clibc_map_node *rotate_right(clibc_map_node *h);
void flip_colors(clibc_map_node *h);
void *get(clibc_map *m, clibc_map_node *x, void *key);
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

clibc_map_node *put(clibc_map *m, clibc_map_node *h, void *key, void *val) {
  int cmp;

  if (h == NULL) {
    return new (m, key, val, RED);
  }
  cmp = m->cmp_fn(key, h->key);
  if (cmp < 0) {
    h->left = put(m, h->left, key, val);
  } else if (cmp > 0) {
    h->right = put(m, h->right, key, val);
  } else {
    memcpy(h->val, val, m->val_sz);
  }

  if (is_red(h->right) && !is_red(h->left)) {
    h = rotate_left(h);
  }
  if (is_red(h->left) && is_red(h->left->left)) {
    h = rotate_right(h);
  }
  if (is_red(h->left) && is_red(h->right)) {
    flip_colors(h);
  }
  return h;
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

int is_red(clibc_map_node *x) {
  if (x == NULL) {
    return 0;
  }
  return x->color == RED;
}

clibc_map_node *rotate_left(clibc_map_node *h) {
  clibc_map_node *x = h->right;

  h->right = x->left;
  x->left = h;
  x->color = h->color;
  h->color = RED;
  return x;
}

clibc_map_node *rotate_right(clibc_map_node *h) {
  clibc_map_node *x = h->left;

  h->left = x->right;
  x->right = h;
  x->color = h->color;
  h->color = RED;
  return x;
}

void flip_colors(clibc_map_node *h) {
  h->color = RED;
  h->left->color = BLACK;
  h->right->color = BLACK;
}

void *get(clibc_map *m, clibc_map_node *x, void *key) {
  int cmp;

  if (x == NULL) {
    return NULL;
  }
  cmp = m->cmp_fn(key, x->key);
  if (cmp < 0) {
    return get(m, x->left, key);
  }
  if (cmp > 0) {
    return get(m, x->right, key);
  }
  return x->val;
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
