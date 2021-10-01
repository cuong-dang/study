#include <assert.h>

#include "../lib/csapp.h"

#define SH_PATH "/bin/sh"

int mysystem(char *command);

int main() {
        assert(mysystem("ls -al") == 0);
        assert(mysystem("asdf") == 127);
        return 0;
}

int mysystem(char *command) {
        pid_t pid;
        char *execve_argv[4];
        int status;

        execve_argv[0] = SH_PATH;
        execve_argv[1] = "-c";
        execve_argv[2] = command;
        execve_argv[3] = NULL;
        if ((pid = Fork()) == 0) {
                Execve(SH_PATH, execve_argv, environ);
        }
        Waitpid(pid, &status, 0);
        if (WIFEXITED(status)) {
                return WEXITSTATUS(status);
        } else {
                return WTERMSIG(status);
        }

}
