#include "ch1.h"

int ex114(int n) {
  int i = 1, rv = i;

  for (i = 2; i * i <= n; i++) {
    rv = i;
  }
  return rv;
}
