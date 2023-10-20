#include "clibc_array.h"
#include "clibc_map.h"
#include <assert.h>
#include <string.h>

int cmp(void *key1, void *key2);
void assert_key(clibc_array *keys, int i, char *k);

int test_clibc_map_keys() {
  clibc_map *m = clibc_map_new(sizeof(char *), sizeof(int), cmp);
  int v;
  clibc_array *keys;

  clibc_map_put(m, "S", &v);
  clibc_map_put(m, "E", &v);
  clibc_map_put(m, "A", &v);
  clibc_map_put(m, "R", &v);
  clibc_map_put(m, "C", &v);
  clibc_map_put(m, "H", &v);

  keys = clibc_array_new(sizeof(char *));
  clibc_map_keys(m, keys);
  assert(keys->size == 6);
  assert_key(keys, 0, "A");
  assert_key(keys, 1, "C");
  assert_key(keys, 2, "E");
  assert_key(keys, 3, "H");
  assert_key(keys, 4, "R");
  assert_key(keys, 5, "S");

  clibc_map_free(m);
  clibc_array_free(keys);
  return 0;
}

void assert_key(clibc_array *keys, int i, char *k) {
  assert(strcmp(clibc_array_get(keys, i), k) == 0);
}
