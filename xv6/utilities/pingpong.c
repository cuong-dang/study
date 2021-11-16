#include "kernel/types.h"
#include "user.h"

int
main(void)
{
  int pipe0[2], pipe1[2], pid;
  char buf[1];

  pipe(pipe0);
  pipe(pipe1);
  pid = fork();

  if(pid == 0){
    pid = getpid();
    close(pipe0[1]);
    close(pipe1[0]);
    if (read(pipe0[0], buf, 1) > 0)
      printf("%d: received ping\n", pid);
    close(pipe0[0]);
    write(pipe1[1], buf, 1);
    close(pipe1[1]);
  } else {
    close(pipe0[0]);
    close(pipe1[1]);
    write(pipe0[1], buf, 1);
    close(pipe0[1]);
    if (read(pipe1[0], buf, 1) > 0)
      printf("%d: received pong\n", pid);
    close(pipe1[0]);
  }
  exit(0);
}
