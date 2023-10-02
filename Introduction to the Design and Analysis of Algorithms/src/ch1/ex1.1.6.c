#include <stdio.h>

int gcd(int a, int b) {
  int r;

  if (b == 0) {
    r = a;
  } else {
    r = gcd(b, a % b);
  }
  printf("gcd(%d, %d) = %d\n", a, b, r);
  return r;
}

int main() { gcd(31415, 14142); }
