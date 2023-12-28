#include "kernel/types.h"
#include "kernel/stat.h"
#include "kernel/fcntl.h"
#include "kernel/fs.h"
#include "user/user.h"

#define MAX_PATH 512

void find(char *path, char *name) {
  int fd, fd2;
  struct stat st;
  struct dirent de;
  char buf[MAX_PATH];
  uint buflen;
  char *p;

  if ((fd = open(path, O_RDONLY)) < 0) {
    fprintf(2, "find: cannot open %s\n", path);
    exit(2);
  }
  if (fstat(fd, &st) < 0) {
    fprintf(2, "find: cannot stat %s\n", path);
    close_(fd);
    exit(3);
  }
  switch (st.type) {
  case T_DIR:
    while (read(fd, &de, sizeof(de)) == sizeof(de)) {
      if (de.inum == 0 || strcmp(de.name, ".") == 0 ||
          strcmp(de.name, "..") == 0) {
        continue;
      }
      strcpy(buf, path);
      buflen = strlen(buf);
      // dir + / + file + \0
      if (buflen + 1 + DIRSIZ + 1 > MAX_PATH) {
        fprintf(2, "find: path too long %s/%s\n", path, de.name);
        continue;
      }
      p = buf + buflen;
      *p++ = '/';
      memmove(p, de.name, DIRSIZ);
      *(p + DIRSIZ) = '\0';
      if ((fd2 = open(buf, O_RDONLY)) < 0) {
        fprintf(2, "find: cannot open %s\n", buf);
        continue;
      }
      if (fstat(fd2, &st) < 0) {
        fprintf(2, "find: cannot stat %s\n", buf);
        close_(fd2);
        continue;
      }
      switch (st.type) {
      case T_FILE:
        if (strcmp(de.name, name) == 0) {
          printf("%s\n", buf);
        }
        break;
      case T_DIR:
        find(buf, name);
        break;
      }
      close_(fd2);
    }
    break;
  default:
    fprintf(2, "find: %s is not a directory\n", path);
    close_(fd);
    exit(4);
  }
  close_(fd);
}

int main(int argc, char *argv[]) {
  if (argc != 3) {
    printf("usage: find <dir> <name>\n");
    exit(1);
  }
  find(argv[1], argv[2]);
  exit(0);
}
