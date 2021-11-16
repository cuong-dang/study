#include "kernel/types.h"
#include "kernel/param.h"
#include "user/user.h"

int
main(int argc, char *argv[])
{
  char buf[512];
  int i, j, k, buflen, arg_i;
  char *xargv[MAXARG];

  if (argc < 2) {
    fprintf(2, "usage: xargs cmd [args]\n");
    exit(1);
  }

  arg_i = 0;
  for (i = 1; i < argc; i++)
    xargv[arg_i++] = argv[i];

  i = 0;
  while (read(0, buf+i, 1) > 0) {
    while (buf[i++] != '\n')
      read(0, buf+i, 1);
    buf[--i] = '\0';

    buflen = strlen(buf);
    for (j = 0, k = arg_i; j < buflen; ) {
      while (j < buflen && buf[j] == ' ')
        j++;
      xargv[k++] = &buf[j];
      while (j < buflen && buf[j] != ' ')
        j++;
      buf[j++] = '\0';
    }

    xargv[k] = 0;
    if (fork() == 0) {
      exec(xargv[0], xargv);
      printf("cannot execute %s\n", xargv[0]);
      exit(1);
    }
    wait(0);
    i = 0;
  }
  exit(0);
}
