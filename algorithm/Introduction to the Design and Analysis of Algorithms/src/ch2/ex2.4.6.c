#include <stdio.h>

/** Restricted Tower of Hanoi. */
void restricted_toh(int n, char src, char aux, char dst) {
  if (n == 1) {
    printf("%c --> %c\n", src, aux);
    printf("%c --> %c\n", aux, dst);
    return;
  }
  restricted_toh(n - 1, src, aux, dst);
  printf("%c --> %c\n", src, aux);
  restricted_toh(n - 1, dst, aux, src);
  printf("%c --> %c\n", aux, dst);
  restricted_toh(n - 1, src, aux, dst);
}

int main() {
  restricted_toh(3, 'A', 'B', 'C');
  return 0;
}
