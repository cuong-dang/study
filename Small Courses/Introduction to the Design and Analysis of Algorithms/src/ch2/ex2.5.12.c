#include <assert.h>
#include <stdio.h>
#include <stdlib.h>

int fib5(int n) {
  if (n == 0)
    return 0;
  if (n == 1)
    return 1;
  return (fib5(n - 1) + fib5(n - 2)) % 100000;
}

int main(int argc, char **argv) {
  assert(fib5(4) == 3);
  assert(fib5(8) == 21);
  assert(fib5(16) == 987);
  assert(fib5(21) == 10946);
  assert(fib5(26) == 21393);

  if (argc == 2) {
    printf("%05d\n", fib5((int)strtol(argv[1], NULL, 10)));
  }
}
