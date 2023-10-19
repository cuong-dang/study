#include "clibc_map.h"
#include <assert.h>

int cmp(void *key1, void *key2);

int test_clibc_map_get() {
  clibc_map *m = clibc_map_new(sizeof(char *), sizeof(int), cmp);
  int v = 0;

  clibc_map_put(m, "S", &v);
  v++;
  clibc_map_put(m, "E", &v);
  v++;
  clibc_map_put(m, "A", &v);
  v++;
  clibc_map_put(m, "R", &v);
  v++;
  clibc_map_put(m, "C", &v);
  v++;
  clibc_map_put(m, "H", &v);

  assert(*(int *)clibc_map_get(m, "S") == 0);
  assert(*(int *)clibc_map_get(m, "E") == 1);
  assert(*(int *)clibc_map_get(m, "A") == 2);
  assert(*(int *)clibc_map_get(m, "R") == 3);
  assert(*(int *)clibc_map_get(m, "C") == 4);
  assert(*(int *)clibc_map_get(m, "H") == 5);

  clibc_map_free(m);
  return 0;
}
