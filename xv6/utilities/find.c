#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "kernel/fs.h"

char *
extract_tail_name(char *path)
{
  char *p;

  for (p = path + strlen(path); p >= path && *p != '/'; --p)
    ;
  return ++p;
}

int
contains_name(char *path, char *name)
{
  if (strcmp(extract_tail_name(path), name) == 0)
    return 1;
  return 0;
}

void
find(char *path, char *key)
{
  int fd;
  struct stat st;
  struct dirent de;
  char buf[512];

  if ((fd = open(path, 0)) < 0) {
    fprintf(2, "find: cannot open %s\n", path);
    return;
  }

  if (fstat(fd, &st) < 0) {
    fprintf(2, "find: cannot stat %s\n", path);
    close(fd);
    return;
  }

  switch (st.type) {
  case T_FILE:
    if (contains_name(path, key))
      printf("%s\n", path);
    break;
  case T_DIR:
    while (read(fd, &de, sizeof(de)) == sizeof(de)) {
      if (de.inum == 0 ||
          strcmp(de.name, ".") == 0 || strcmp(de.name, "..") == 0)
        continue;
      if (strlen(path) + 1 + strlen(de.name) + 1 > sizeof(buf)) {
        fprintf(2, "find: path too long %s/%s\n", path, de.name);
        continue;
      }
      memcpy(buf, path, strlen(path));
      memcpy(buf+strlen(path), "/", 1);
      memcpy(buf+strlen(path)+1, de.name, strlen(de.name));
      buf[strlen(path)+1+strlen(de.name)] = '\0';
      find(buf, key);
    }
  }
  close(fd);
}

int
main(int argc, char *argv[])
{
  if (argc != 3) {
    fprintf(2, "usage: find path name\n");
    exit(1);
  }
  find(argv[1], argv[2]);
  exit(0);
}
