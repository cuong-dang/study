#include "assert_macro.h"
#include "ch1.h"

int test_ex1110(int argc, char **argv) {
  int errcode = 0;
  assertEqual(ex1110(1, 0) == gcd(1, 0));
  assertEqual(ex1110(4, 2) == gcd(4, 2));
  assertEqual(ex1110(9, 6) == gcd(9, 6));
  assertEqual(ex1110(31415, 14142) == gcd(31415, 14142));
  return errcode;
}
