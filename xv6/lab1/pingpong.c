#include "user/user.h"

#define BUFSIZE 1

int main() {
  int pipe1[2], pipe2[2], pid;
  char buf[BUFSIZE];

  pipe(pipe1);
  pipe(pipe2);
  pid = fork();

  if (pid == 0) {
    pid = getpid();
    close(pipe1[1]);
    close(pipe2[0]);
    read(pipe1[0], buf, BUFSIZE);
    printf("%d: received ping\n", pid);
    write(pipe2[1], buf, BUFSIZE);
    close(pipe1[0]);
    close(pipe2[1]);
    exit(0);
  }

  pid = getpid();
  close(pipe1[0]);
  close(pipe2[1]);
  write(pipe1[1], buf, BUFSIZE);
  read(pipe2[0], buf, BUFSIZE);
  printf("%d: received pong\n", pid);
  close(pipe1[1]);
  close(pipe2[0]);
  exit(0);
}
