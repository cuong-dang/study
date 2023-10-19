#ifndef CLIBC_MAP
#define CLIBC_MAP
#include <stddef.h>

typedef int clibc_map_node_color;
typedef struct node {
  void *key;
  void *val;
  clibc_map_node_color color;

  struct node *left;
  struct node *right;
} clibc_map_node;

typedef int cmp_fn(void *, void *);
typedef struct {
  size_t key_sz;
  size_t val_sz;
  clibc_map_node *root;

  cmp_fn *cmp_fn;
} clibc_map;

clibc_map *clibc_map_new(size_t key_sz, size_t val_sz, cmp_fn *cmp_fn);
void clibc_map_put(clibc_map *m, void *key, void *val);
void *clibc_map_get(clibc_map *m, void *key);
void clibc_map_free(clibc_map *m);

#endif
