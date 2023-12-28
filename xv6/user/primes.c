#include "kernel/types.h"
#include "user/user.h"

#define N 35

int main(void) {
  int i, left[2], n, p = 0, right[2];

  pipe_(left);
  for (i = 2; i <= N; i++) {
    write_(left[1], &i, sizeof(int));
  }
  if (fork_() == 0) {
    close_(left[1]);
    while (read(left[0], &n, sizeof(int)) != 0) {
      if (p == 0) {
        p = n;
        printf("prime %d\n", p);
        pipe_(right);
        if (fork_() == 0) {
          close_(left[0]);
          close_(right[1]);
          p = 0;
          left[0] = right[0];
        }
      } else if (n % p != 0) {
        write_(right[1], &n, sizeof(int));
      }
    }
    close_(left[0]);
    if (p != 0) {
      close_(right[1]);
    }
    wait((int *)0);
  } else {
    close_(left[0]);
    close_(left[1]);
    wait((int *)0);
  }
  exit(0);
}
