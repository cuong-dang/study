#include "cc_algs_str.h"
#include <assert.h>

int test_cc_algs_str_horspool_match() {
  assert(horspool_match("", "") == -1);
  assert(horspool_match("a", "") == -1);
  assert(horspool_match("", "a") == -1);
  assert(horspool_match("a", "a") == 0);
  assert(horspool_match("a", "b") == -1);

  assert(horspool_match("abc", "ab") == 0);
  assert(horspool_match("abc", "bc") == 1);
  assert(horspool_match("abc", "c") == 2);

  assert(horspool_match("JIM SAW ME IN A BARBERSHOP", "BARBER") == 16);
  assert(horspool_match("BESS KNEW ABOUT BAOBABS", "BAOBAB") == 16);
  return 0;
}
