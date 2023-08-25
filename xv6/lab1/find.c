#include "user/user.h"
#include "kernel/stat.h"
#include "kernel/fs.h"

void find(char *path, char *file_name) {
  int fd;
  struct stat st;
  char buf[512], *p;
  struct dirent de;

  if ((fd = open(path, 0)) < 0) {
    fprintf(2, "find: cannot open %s\n", path);
    exit(1);
  }
  if (fstat(fd, &st) < 0) {
    fprintf(2, "find: cannot stat %s\n", path);
    close(fd);
    exit(1);
  }
  switch (st.type) {
  case T_FILE:
    fprintf(2, "find: %s is not a directory\n", path);
    close(fd);
    exit(1);
  case T_DIR:
    if (strlen(path) + 1 + DIRSIZ + 1 > sizeof(buf)) {
      fprintf(2, "find: path too long\n");
      exit(1);
    }
    strcpy(buf, path);
    p = buf + strlen(buf);
    *p++ = '/';
    while (read(fd, &de, sizeof(de)) == sizeof(de)) {
      if (de.inum == 0 || !strcmp(de.name, ".") || !strcmp(de.name, "..")) {
        continue;
      }
      strcpy(p, de.name);
      if (stat(buf, &st) < 0) {
        fprintf(2, "find: cannot stat %s\n", buf);
        continue;
      }
      if (st.type == T_FILE && !strcmp(de.name, file_name)) {
        printf("%s\n", buf);
      } else if (st.type == T_DIR) {
        find(buf, file_name);
      }
    }
  }
  close(fd);
}

int main(int argc, char *argv[]) {
  if (argc != 3) {
    fprintf(2, "usage: find dirpath filename\n");
    exit(1);
  }
  find(argv[1], argv[2]);
  exit(0);
}
