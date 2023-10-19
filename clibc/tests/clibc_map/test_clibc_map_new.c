#include "clibc_map.h"
#include <assert.h>
#include <string.h>

int cmp(void *key1, void *key2) { return strcmp((char *)key1, (char *)key2); }

int test_clibc_map_new() {
  clibc_map *m = clibc_map_new(sizeof(char *), sizeof(int), cmp);

  assert(m->key_sz == sizeof(char *));
  assert(m->val_sz == sizeof(int));
  assert(m->cmp_fn == cmp);
  return 0;
}
