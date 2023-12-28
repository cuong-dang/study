#include "kernel/types.h"
#include "user/user.h"

int main() {
  int p_writes[2], c_writes[2];
  char buf[1];

  pipe_(p_writes);
  pipe_(c_writes);
  if (fork_() == 0) {
    close_(p_writes[1]);
    close_(c_writes[0]);
    read(p_writes[0], buf, 1);
    printf("%d: received ping\n", getpid());
    close_(p_writes[0]);
    write(c_writes[1], buf, 1);
    close_(c_writes[1]);
  } else {
    close_(p_writes[0]);
    close_(c_writes[1]);
    write(p_writes[1], buf, 1);
    close_(p_writes[1]);
    read(c_writes[0], buf, 1);
    close_(c_writes[0]);
    printf("%d: received pong\n", getpid());
  }
  exit(0);
}
