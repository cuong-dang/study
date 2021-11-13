#include <time.h>
#include <math.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

#define RUN_SECS 4
#define BUF_SIZE 1

int main() {
    int xcount, pipe0[2], pipe1[2], pid, i;
    char buf[BUF_SIZE];
    clock_t begin, end;
    int elapsed_time;

    pipe(pipe0);
    pipe(pipe1);
    pid = fork();
    if (pid == 0) {
        close(pipe0[1]);
        close(pipe1[0]);
        while (read(pipe0[0], buf, BUF_SIZE) > 0)
            write(pipe1[1], buf, BUF_SIZE);
        close(pipe0[0]);
        close(pipe1[1]);
        exit(0);
    }
    close(pipe0[0]);
    close(pipe1[1]);
    xcount = 0;
    begin = clock();
    for (i = 0; i < RUN_SECS; ) {
        write(pipe0[1], buf, BUF_SIZE);
        read(pipe1[0], buf, BUF_SIZE);
        xcount += 2;
        end = clock();
        elapsed_time = (int)(double)(end - begin) / CLOCKS_PER_SEC;
        if (elapsed_time >= 1) {
            i += elapsed_time;
            printf("Number of exchanges in the last second: %.0f\n",
                    round(xcount / elapsed_time));
            xcount = 0;
            begin = clock();
        }
    }
    close(pipe0[1]);
    close(pipe1[0]);
    exit(0);
}
