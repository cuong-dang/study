#include "assert_macro.h"
#include "ch1.h"

int test_ex1111b(int argc, char **argv) {
  int ec = 0, x, y;

  ex1111b(2, 0, 2, &x, &y);
  assertEqual(x == 1);
  assertEqual(y == 0);

  ex1111b(2, 3, 5, &x, &y);
  printf("%d %d\n", x, y);
  assertEqual(x == -5);
  assertEqual(y == 5);
  return ec;
}
