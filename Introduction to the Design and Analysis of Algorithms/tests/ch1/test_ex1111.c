#include "assert_macro.h"
#include "ch1.h"

int test_ex1111(int argc, char **argv) {
  int ec = 0;
  int g, x, y;
  g = ex1111(30, 20, &x, &y);
  assertEqual(g == 10);
  assertEqual(x == 1);
  assertEqual(y == -1);

  g = ex1111(35, 15, &x, &y);
  assertEqual(g == 5);
  assertEqual(x == 1);
  assertEqual(y == -2);
  return ec;
}