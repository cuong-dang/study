#include "assert_macro.h"
#include "ch1.h"
#include <stdlib.h>

int test_ex115(int argc, char **argv) {
  int errcode = 0, t;
  int a[] = {2, 5, 5, 5}, b[] = {2, 2, 3, 5, 5, 7};
  int *c = malloc(3 * sizeof(int));

  ex115(a, 4, b, 6, c, &t);
  assertEqual(t = 3);
  assertEqual(c[0] == 2);
  assertEqual(c[1] == 5);
  assertEqual(c[2] == 5);
  return errcode;
}
