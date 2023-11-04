#include "cc_algs_np.h"
#include <assert.h>

int test_cc_algs_np_nqueens() {
  int out[5];

  assert(nqueens(1, out) == 1);
  assert(out[0] == 0);
  assert(nqueens(2, out) == 0);
  assert(nqueens(3, out) == 0);
  assert(nqueens(4, out) == 1);
  assert(out[0] == 1);
  assert(out[1] == 3);
  assert(out[2] == 0);
  assert(out[3] == 2);
  assert(nqueens(5, out) == 1);
  assert(out[0] == 0);
  assert(out[1] == 2);
  assert(out[2] == 4);
  assert(out[3] == 1);
  assert(out[4] == 3);
  return 0;
}
