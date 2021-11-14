#include "kernel/types.h"
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/wait.h>

#define N 35

int
main(void)
{
  int right[2], pid, i, has_right, left[2], p, n;

  pipe(right);
  pid = fork();
  if (pid != 0) {
    close(right[0]);
    for (i = 2; i <= N; i++)
      write(right[1], &i, sizeof(int));
    close(right[1]);
  } else {
    has_right = 0;
    left[0] = right[0]; left[1] = right[1];
    close(left[1]);
    while (read(left[0], &p, sizeof(int)) > 0) {
      if (!has_right) {
        n = p;
        printf("prime %d\n", n);
        pipe(right);
        if (fork() == 0) {
          has_right = 0;
          left[0] = right[0]; left[1] = right[1];
          close(left[1]);
        } else {
          close(right[0]);
          has_right = 1;
        }
      } else if (p % n)
        write(right[1], &p, sizeof(int));
    }
    close(left[0]);
    close(right[1]);
    wait(0);
  }
  wait(0);
  exit(0);
}
