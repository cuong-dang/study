#include "assert_macro.h"
#include "ch1.h"

int test_ex114(int argc, char **argv) {
  int errcode = 0;

  assertEqual(floor_sqrt(1) == 1);
  assertEqual(floor_sqrt(2) == 1);
  assertEqual(floor_sqrt(3) == 1);
  assertEqual(floor_sqrt(4) == 2);

  assertEqual(floor_sqrt(7) == 2);
  assertEqual(floor_sqrt(9) == 3);

  assertEqual(floor_sqrt(13) == 3);
  assertEqual(floor_sqrt(16) == 4);
  return errcode;
}
