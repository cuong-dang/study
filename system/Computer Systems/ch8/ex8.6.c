#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[], char *envp[]) {
        int i;

        printf("Command-line arguments:\n");
        for (i = 1; i < argc; i++) {
                printf("  argv[%2d]: %s\n", i, argv[i]);
        }
        printf("Environment variables:\n");
        for (i = 0; envp[i]; i++) {
                printf("  envp[%2d]: %s=%s\n", i, envp[i], getenv(envp[i]));
        }
        return 0;
}
