#include "kernel/types.h"
#include "user/user.h"

#define N 35

int main() {
  int p = 0, n, left[2], right[2];

  pipe(right);
  if (fork() != 0) {
    /* Init pipeline */
    close(right[0]);
    for (n = 2; n <= N; n++) {
      write(right[1], &n, sizeof(int));
    }
    close(right[1]);
  } else {
    /* Subsequent sieves */
    left[0] = right[0];
    left[1] = right[1];
    close(left[1]);
    while (read(left[0], &n, sizeof(int)) > 0) {
      if (p == 0) {
        p = n;
        printf("prime %d\n", p);

        pipe(right);
        if (fork() == 0) {
          p = 0;
          left[0] = right[0];
          left[1] = right[1];
          close(left[1]);
        } else {
          close(right[0]);
        }
      } else if (n % p) {
        write(right[1], &n, sizeof(int));
      }
    }
    close(left[0]);
    close(right[1]);
  }
  wait(0);
  exit(0);
}
