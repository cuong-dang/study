#include "cc_algs_np.h"
#include <assert.h>

int test_cc_algs_np_subset_sum() {
  int a[4] = {1};
  int out[4];

  assert(subset_sum(1, a, 1, out) == 1);
  assert(out[0] == 1);
  assert(subset_sum(2, a, 1, out) == 0);
  a[1] = 2;
  assert(subset_sum(1, a, 2, out) == 1);
  assert(out[0] == 1);
  assert(out[1] == 0);
  assert(subset_sum(2, a, 2, out) == 1);
  assert(out[0] == 0);
  assert(out[1] == 1);
  assert(subset_sum(3, a, 2, out) == 1);
  assert(out[0] == 1);
  assert(out[1] == 1);
  assert(subset_sum(4, a, 2, out) == 0);

  a[0] = 3;
  a[1] = 5;
  a[2] = 6;
  a[3] = 7;
  assert(subset_sum(15, a, 4, out) == 1);
  assert(out[0] == 1);
  assert(out[1] == 1);
  assert(out[2] == 0);
  assert(out[3] == 1);
  return 0;
}
