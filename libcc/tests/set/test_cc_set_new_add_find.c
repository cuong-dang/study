#include "cc_set.h"
#include <assert.h>
#include <string.h>

int cmp(void *e1, void *e2);

int test_cc_set_new_add_find() {
  cc_set *s;
  char *key;

  s = cc_set_new(sizeof(char **), cmp);
  key = "a";
  assert(cc_set_contains(s, &key) == 0);
  cc_set_add(s, &key);
  assert(cc_set_contains(s, &key) == 1);

  cc_set_free(s);
  return 0;
}

int cmp(void *e1, void *e2) { return strcmp(*(char **)e1, *(char **)e2); }
