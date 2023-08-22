#include <math.h>
#include <stdio.h>
#include <time.h>
#include <unistd.h>

#define BUFSIZE 1
#define CHECK_EVERY 512

int main() {
    int count, pipe1[2], pipe2[2], pid, num_passes;
    char buf[BUFSIZE];
    time_t start, end;
    double diff;

    pipe(pipe1);
    pipe(pipe2);
    pid = fork();

    if (pid == 0) {
        close(pipe1[1]);
        close(pipe2[0]);
        while (1) {
            read(pipe1[0], buf, BUFSIZE);
            write(pipe2[1], buf, BUFSIZE);
        }
    }

    close(pipe1[0]);
    close(pipe2[1]);
    while (1) {
        num_passes = 1;
        count = 0;
        time(&start);
        while (1) {
            write(pipe1[1], buf, BUFSIZE);
            read(pipe2[0], buf, BUFSIZE);
            count += 1;
            if (count > num_passes * CHECK_EVERY) {
                time(&end);
                diff = difftime(end, start);
                if (diff > 1) {
                    printf("Number of bytes per sec: %.0f\n",
                           round(2 * BUFSIZE * count / diff));
                    break;
                }
                num_passes += 1;
            }
        }
    }
}
