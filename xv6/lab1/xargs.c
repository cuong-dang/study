#include "user/user.h"
#include "kernel/param.h"

#define BUFSIZ 512

void alloc_copy(char *args[], int i, char *s) {
  if ((args[i] = (char *)malloc(strlen(s) + 1)) < 0) {
    fprintf(2, "xargs: malloc failed\n");
    exit(1);
  }
  strcpy(args[i], s);
}

void xarg(int j, char *buf, char *args[]) {}

int main(int argc, char *argv[]) {
  char *args[MAXARG], buf[BUFSIZ], c;
  int i = 0, j, k = 0, pid;

  if (argc < 2) {
    fprintf(2, "usage: xargs cmd arg1 arg2 ...\n");
    exit(1);
  }
  alloc_copy(args, i, argv[1]);
  for (i = 2; i < argc; i++) {
    alloc_copy(args, i - 1, argv[i]);
  }
  i -= 1;
  j = i;
  while (read(0, &c, 1) > 0) {
    if (c == ' ') {
      if (k != 0) {
        buf[k] = '\0';
        alloc_copy(args, j++, buf);
        k = 0;
      }
      continue;
    } else if (c != '\n') {
      if (k == BUFSIZ - 1) {
        fprintf(2, "xargs: some arg too long\n");
        exit(1);
      }
      buf[k++] = c;
    } else {
      if (j == MAXARG - 1) {
        fprintf(2, "xargs: too many args\n");
        exit(1);
      }
      buf[k] = '\0';
      alloc_copy(args, j++, buf);
      args[j] = 0;
      if ((pid = fork()) < 0) {
        fprintf(2, "xargs: fork failed\n");
        exit(1);
      }
      if (pid == 0) {
        exec(args[0], args);
        fprintf(2, "xargs: exec failed\n");
        exit(1);
      } else {
        wait(0);
        for (k = i; k < j; k++) {
          free(args[k]);
        }
        j = i;
        k = 0;
      }
    }
  }
  for (i = 0; i < argc - 1; i++) {
    free(args[i]);
  }
  exit(0);
}
