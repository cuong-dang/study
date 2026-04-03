#include <assert.h>

int pow_(int a, int n) {
  int sqrt;

  if (n == 1) {
    return a;
  }
  sqrt = pow_(a, n / 2);
  if (n % 2 == 0) {
    return sqrt * sqrt;
  }
  return sqrt * sqrt * a;
}

int main() {
  assert(pow_(2, 1) == 2);
  assert(pow_(2, 2) == 4);
  assert(pow_(3, 3) == 27);
  assert(pow_(4, 4) == 256);
  assert(pow_(2, 5) == 32);
}
