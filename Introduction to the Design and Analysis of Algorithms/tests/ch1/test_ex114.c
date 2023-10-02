#include "assert_macro.h"
#include "ch1.h"

int test_ex114(int argc, char **argv) {
  int errcode = 0;

  assertEqual(ex114(1) == 1);
  assertEqual(ex114(2) == 1);
  assertEqual(ex114(3) == 1);
  assertEqual(ex114(4) == 2);

  assertEqual(ex114(7) == 2);
  assertEqual(ex114(9) == 3);

  assertEqual(ex114(13) == 3);
  assertEqual(ex114(16) == 4);
  return errcode;
}
