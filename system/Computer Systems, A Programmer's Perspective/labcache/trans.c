/*
 * trans.c - Matrix transpose B = A^T
 *
 * Each transpose function must have a prototype of the form:
 * void trans(int M, int N, int A[N][M], int B[M][N]);
 *
 * A transpose function is evaluated by counting the number of misses
 * on a 1KB direct mapped cache with a block size of 32 bytes.
 */
#include <stdio.h>
#include "cachelab.h"

int is_transpose(int M, int N, int A[N][M], int B[M][N]);

/*
 * transpose_submit - This is the solution transpose function that you
 *     will be graded on for Part B of the assignment. Do not change
 *     the description string "Transpose submission", as the driver
 *     searches for that string to identify the transpose function to
 *     be graded.
 */
char transpose_submit_desc[] = "Transpose submission";
void transpose_submit(int M, int N, int A[N][M], int B[M][N])
{
#define BLOCK_SIZE 8
    int i, j, ii, jj;
    /* Temp variables to reduce conflict misses. */
    int t0, t1, t2, t3, t4, t5, t6, t7;

    for (i = 0; i < N-BLOCK_SIZE+1; i += BLOCK_SIZE) {
        for (j = 0; j < M-BLOCK_SIZE+1; j += BLOCK_SIZE) {
            for (ii = i; ii < i+BLOCK_SIZE; ii++) {
                for (jj = j; jj < j+BLOCK_SIZE; jj += BLOCK_SIZE) {
                    t0 = A[ii][jj+0];
                    t1 = A[ii][jj+1];
                    t2 = A[ii][jj+2];
                    t3 = A[ii][jj+3];
                    t4 = A[ii][jj+4];
                    t5 = A[ii][jj+5];
                    t6 = A[ii][jj+6];
                    t7 = A[ii][jj+7];

                    B[jj+0][ii] = t0;
                    B[jj+1][ii] = t1;
                    B[jj+2][ii] = t2;
                    B[jj+3][ii] = t3;
                    B[jj+4][ii] = t4;
                    B[jj+5][ii] = t5;
                    B[jj+6][ii] = t6;
                    B[jj+7][ii] = t7;
                }
            }
        }
        /* Finish off rest elements for "odd" matrix sizes. */
        if (j == M) continue;
        for (j = j-BLOCK_SIZE+1; j < M; j++) {
            for (ii = i; ii < i+BLOCK_SIZE; ii++) {
                for (jj = j; jj < j+BLOCK_SIZE; jj += BLOCK_SIZE) {
                    t0 = A[ii][jj+0];
                    t1 = A[ii][jj+1];
                    t2 = A[ii][jj+2];
                    t3 = A[ii][jj+3];
                    t4 = A[ii][jj+4];
                    t5 = A[ii][jj+5];
                    t6 = A[ii][jj+6];
                    t7 = A[ii][jj+7];

                    B[jj+0][ii] = t0;
                    B[jj+1][ii] = t1;
                    B[jj+2][ii] = t2;
                    B[jj+3][ii] = t3;
                    B[jj+4][ii] = t4;
                    B[jj+5][ii] = t5;
                    B[jj+6][ii] = t6;
                    B[jj+7][ii] = t7;
                }
            }
        }
    }
    if (i == N) return;
    for (i = i-BLOCK_SIZE+1; i < N; i++) {
        for (j = 0; j < M; j++) {
            B[j][i] = A[i][j];
        }
    }
}

/*
 * You can define additional transpose functions below. We've defined
 * a simple one below to help you get started.
 */

/*
 * trans - A simple baseline transpose function, not optimized for the cache.
 */
char trans_desc[] = "Simple row-wise scan transpose";
void trans(int M, int N, int A[N][M], int B[M][N])
{
    int i, j, tmp;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; j++) {
            tmp = A[i][j];
            B[j][i] = tmp;
        }
    }
}

/*
 * registerFunctions - This function registers your transpose
 *     functions with the driver.  At runtime, the driver will
 *     evaluate each of the registered functions and summarize their
 *     performance. This is a handy way to experiment with different
 *     transpose strategies.
 */
void registerFunctions()
{
    /* Register your solution function */
    registerTransFunction(transpose_submit, transpose_submit_desc);
}

/*
 * is_transpose - This helper function checks if B is the transpose of
 *     A. You can check the correctness of your transpose by calling
 *     it before returning from the transpose function.
 */
int is_transpose(int M, int N, int A[N][M], int B[M][N])
{
    int i, j;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; ++j) {
            if (A[i][j] != B[j][i]) {
                return 0;
            }
        }
    }
    return 1;
}
