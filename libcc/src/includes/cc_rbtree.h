#ifndef CC_RBTREE
#define CC_RBTREE
#include "cc_array.h"
#include <stddef.h>

typedef int cc_rbtree_node_color;
typedef struct node {
  void *key;
  void *val;
  cc_rbtree_node_color color;

  struct node *left;
  struct node *right;
} cc_rbtree_node;

typedef int cmpfn(void *, void *);
typedef struct {
  size_t key_sz;
  size_t val_sz;
  cc_rbtree_node *root;

  cmpfn *cmpfn;
} cc_rbtree;

cc_rbtree *cc_rbtree_new(size_t key_sz, size_t val_sz, cmpfn *cmpfn);
void cc_rbtree_add(cc_rbtree *t, void *key, void *val);
void *cc_rbtree_get(cc_rbtree *t, void *key);
void cc_rbtree_rm_min(cc_rbtree *t, void *keyout, void *valout);
void cc_rbtree_keys(cc_rbtree *t, cc_array *keys);
void cc_rbtree_free(cc_rbtree *t);

#endif
