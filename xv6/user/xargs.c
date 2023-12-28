#include "kernel/types.h"
#include "user/user.h"
#include "kernel/param.h"

#define BUF_SIZE 512

int main(int argc, char *argv[]) {
  char *xargs[MAXARG], buf[BUF_SIZE], *p;
  int i, n;

  if (argc < 2) {
    printf("usage: xargs <command> [args]\n");
    exit(1);
  }

  // Prep args
  xargs[0] = argv[1];
  for (i = 2, n = 1; i < argc; i++, n++) {
    xargs[n] = argv[i];
  }
  p = buf;
  while (read(0, p++, 1) != 0) {
    // WARN: Not trimming white spaces.
    if ((p - 1) == buf || *(p - 2) == '\0') {
      xargs[n++] = p - 1;
    } else if (*(p - 1) == ' ') {
      *(p - 1) = '\0';
    } else if (*(p - 1) == '\n') {
      *(p - 1) = '\0';
      xargs[n] = 0;
      if (fork_() == 0) {
        exec(argv[1], xargs);
        fprintf(2, "exec %s failed\n", argv[1]);
        exit(2);
      } else {
        wait(0);
        p = buf;
        n = i - 1;
      }
    }
    if (p == buf + BUF_SIZE) {
      fprintf(2, "arg line too long\n");
      exit(3);
    }
  }
  exit(0);
}
