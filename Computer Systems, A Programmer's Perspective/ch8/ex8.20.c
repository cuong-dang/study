#include "../lib/csapp.h"

int main(int argc, char *argv[]) {
        Execve("/bin/ls", argv, environ);
        return 0;
}
