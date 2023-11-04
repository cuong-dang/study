#include "cc_array.h"
#include "cc_rbtree.h"
#include <stdlib.h>
#include <string.h>

#define RED 0
#define BLACK 1

int size(cc_rbtree_node *n);
cc_rbtree_node *add(cc_rbtree *t, cc_rbtree_node *n, void *key, void *val);
cc_rbtree_node *new(cc_rbtree *t, void *key, void *val, int color);
int is_red(cc_rbtree_node *n);
cc_rbtree_node *rotate_left(cc_rbtree_node *n);
cc_rbtree_node *rotate_right(cc_rbtree_node *n);
void flip_colors(cc_rbtree_node *n);
void *get(cc_rbtree *t, cc_rbtree_node *n, void *key);
cc_rbtree_node *rm_min(cc_rbtree *t, cc_rbtree_node *n, void *keyout,
                       void *valout);
cc_rbtree_node *mv_red_left(cc_rbtree_node *n);
cc_rbtree_node *balance(cc_rbtree_node *n);
void get_keys(cc_array *keys, cc_rbtree_node *n);
void node_free(cc_rbtree_node *n);

cc_rbtree *cc_rbtree_new(size_t key_sz, size_t val_sz, cmpfn *cmpfn) {
  cc_rbtree *t = malloc(sizeof(cc_rbtree));

  t->key_sz = key_sz;
  t->val_sz = val_sz;
  t->cmpfn = cmpfn;
  t->root = NULL;
  return t;
}

int cc_rbtree_size(cc_rbtree *t) { return size(t->root); }

void cc_rbtree_add(cc_rbtree *t, void *key, void *val) {
  t->root = add(t, t->root, key, val);
  t->root->color = BLACK;
}

void *cc_rbtree_get(cc_rbtree *t, void *key) { return get(t, t->root, key); }

void cc_rbtree_rm_min(cc_rbtree *t, void *keyout, void *valout) {
  void *out;

  if (!is_red(t->root->left) && !is_red(t->root->right)) {
    t->root->color = RED;
  }
  t->root = rm_min(t, t->root, keyout, valout);
  if (t->root != NULL) {
    t->root->color = BLACK;
  }
}

void cc_rbtree_keys(cc_rbtree *t, cc_array *keys) {
  cc_array_clear(keys);
  get_keys(keys, t->root);
}

void cc_rbtree_free(cc_rbtree *t) {
  node_free(t->root);
  free(t);
}

int size(cc_rbtree_node *n) {
  if (n == NULL) {
    return 0;
  }
  return n->n;
}

cc_rbtree_node *add(cc_rbtree *t, cc_rbtree_node *n, void *key, void *val) {
  int cmp;

  if (n == NULL) {
    return new (t, key, val, RED);
  }
  cmp = t->cmpfn(key, n->key);
  if (cmp < 0) {
    n->left = add(t, n->left, key, val);
  } else if (cmp > 0) {
    n->right = add(t, n->right, key, val);
  } else {
    memcpy(n->val, val, t->val_sz);
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

  n->n = size(n->left) + size(n->right) + 1;
  return n;
}

cc_rbtree_node *new(cc_rbtree *t, void *key, void *val, int color) {
  cc_rbtree_node *n = malloc(sizeof(cc_rbtree_node));

  n->key = malloc(t->key_sz);
  memcpy(n->key, key, t->key_sz);
  n->val = malloc(t->val_sz);
  memcpy(n->val, val, t->val_sz);
  n->color = color;
  n->left = NULL;
  n->right = NULL;
  n->n = 1;
  return n;
}

int is_red(cc_rbtree_node *n) {
  if (n == NULL) {
    return 0;
  }
  return n->color == RED;
}

cc_rbtree_node *rotate_left(cc_rbtree_node *n) {
  cc_rbtree_node *nn = n->right;

  n->right = nn->left;
  nn->left = n;
  nn->color = n->color;
  n->color = RED;
  nn->n = n->n;
  n->n = size(n->left) + size(n->right) + 1;
  return nn;
}

cc_rbtree_node *rotate_right(cc_rbtree_node *n) {
  cc_rbtree_node *nn = n->left;

  n->left = nn->right;
  nn->right = n;
  nn->color = n->color;
  n->color = RED;
  nn->n = n->n;
  n->n = size(n->left) + size(n->right) + 1;
  return nn;
}

void flip_colors(cc_rbtree_node *n) {
  n->color = RED;
  n->left->color = BLACK;
  n->right->color = BLACK;
}

cc_rbtree_node *rm_min(cc_rbtree *t, cc_rbtree_node *n, void *keyout,
                       void *valout) {
  if (n->left == NULL) {
    memcpy(keyout, n->key, t->key_sz);
    memcpy(valout, n->val, t->val_sz);
    node_free(n);
    return NULL;
  }
  if (!is_red(n->left) && !is_red(n->left->left)) {
    n = mv_red_left(n);
  }
  n->left = rm_min(t, n->left, keyout, valout);
  n->n = size(n->left) + size(n->right) + 1;
  return balance(n);
}

cc_rbtree_node *mv_red_left(cc_rbtree_node *n) {
  flip_colors(n);
  if (is_red(n->right->left)) {
    n->right = rotate_right(n->right);
    n = rotate_left(n);
    flip_colors(n);
  }
  return n;
}

cc_rbtree_node *balance(cc_rbtree_node *n) {
  if (n->left == NULL && n->right != NULL) {
    n->right->color = RED;
  }
  if (is_red(n->right)) {
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

void *get(cc_rbtree *t, cc_rbtree_node *n, void *key) {
  int cmp;

  if (n == NULL) {
    return NULL;
  }
  cmp = t->cmpfn(key, n->key);
  if (cmp < 0) {
    return get(t, n->left, key);
  }
  if (cmp > 0) {
    return get(t, n->right, key);
  }
  return n->val;
}

void get_keys(cc_array *keys, cc_rbtree_node *n) {
  if (n == NULL) {
    return;
  }
  get_keys(keys, n->left);
  cc_array_add(keys, n->key);
  get_keys(keys, n->right);
}

void node_free(cc_rbtree_node *n) {
  if (n == NULL) {
    return;
  }
  free(n->key);
  free(n->val);
  node_free(n->left);
  node_free(n->right);
}
