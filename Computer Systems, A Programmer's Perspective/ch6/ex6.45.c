#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define UNROLL_N 4
#define BLOCK_SIZE 8
#define N_SMALL 8192
#define N_MEDIUM 16384
#define N_LARGE 32768
#define TRANSPOSE0_NAME "original"
#define TRANSPOSE1_NAME "1d4x4 unrolling"
#define TRANSPOSE2_NAME "2d4x4 unrolling"
#define TRANSPOSE3_NAME "32 byte blocking"
#define TRANSPOSE4_NAME TRANSPOSE3_NAME " & " TRANSPOSE2_NAME
#define TEST_DIM 256

/* The given transpose routine */
void transpose0(int *dst, int *src, int dim) {
        int i, j;

        for (i = 0; i < dim; i++)
                for (j = 0; j < dim; j++)
                        dst[j*dim + i] = src[i*dim + j];
}

/* Loop unrolling j only */
void transpose1(int *dst, int *src, int dim) {
        int i, j;

        for (i = 0; i < dim; i++) {
                for (j = 0; j < dim-UNROLL_N+1; j += UNROLL_N) {
                        dst[j*dim + i] = src[i*dim + j];
                        dst[(j+1)*dim + i] = src[i*dim + (j+1)];
                        dst[(j+2)*dim + i] = src[i*dim + (j+2)];
                        dst[(j+3)*dim + i] = src[i*dim + (j+3)];
                }
                for (; j < dim; j++)
                        dst[j*dim + i] = src[i*dim + j];
        }
}

/* Loop unrolling i and j */
void transpose2(int *dst, int *src, int dim) {
        int i, j;

        for (i = 0; i < dim-UNROLL_N+1; i += UNROLL_N) {
                for (j = 0; j < dim-UNROLL_N+1; j += UNROLL_N) {
                        dst[j*dim + i] = src[i*dim + j];
                        dst[(j+1)*dim + i] = src[i*dim + (j+1)];
                        dst[(j+2)*dim + i] = src[i*dim + (j+2)];
                        dst[(j+3)*dim + i] = src[i*dim + (j+3)];

                        dst[j*dim + i+1] = src[(i+1)*dim + j];
                        dst[(j+1)*dim + i+1] = src[(i+1)*dim + (j+1)];
                        dst[(j+2)*dim + i+1] = src[(i+1)*dim + (j+2)];
                        dst[(j+3)*dim + i+1] = src[(i+1)*dim + (j+3)];

                        dst[j*dim + i+2] = src[(i+2)*dim + j];
                        dst[(j+1)*dim + i+2] = src[(i+2)*dim + (j+1)];
                        dst[(j+2)*dim + i+2] = src[(i+2)*dim + (j+2)];
                        dst[(j+3)*dim + i+2] = src[(i+2)*dim + (j+3)];

                        dst[j*dim + i+3] = src[(i+3)*dim + j];
                        dst[(j+1)*dim + i+3] = src[(i+3)*dim + (j+1)];
                        dst[(j+2)*dim + i+3] = src[(i+3)*dim + (j+2)];
                        dst[(j+3)*dim + i+3] = src[(i+3)*dim + (j+3)];
                }
        }
        /* left out finishing off rest of elements as it is too tedious */
}

/* Blocking */
void transpose3(int *dst, int *src, int dim) {
        int ii, jj, i, j;

        for (ii = 0; ii < dim; ii += BLOCK_SIZE)
        for (jj = 0; jj < dim; jj += BLOCK_SIZE)
                for (i = ii; i < ii + BLOCK_SIZE; i++)
                for (j = jj; j < jj + BLOCK_SIZE; j++)
                        dst[j*dim + i] = src[i*dim + j];
}

/* Blocking and loop unrolling */
void transpose4(int *dst, int *src, int dim) {
        int ii, jj, i, j;

        for (ii = 0; ii < dim; ii += BLOCK_SIZE)
        for (jj = 0; jj < dim; jj += BLOCK_SIZE)
                for (i = ii; i < ii+BLOCK_SIZE-UNROLL_N+1; i += UNROLL_N) {
                for (j = jj; j < jj+BLOCK_SIZE-UNROLL_N+1; j += UNROLL_N) {
                        dst[j*dim + i] = src[i*dim + j];
                        dst[(j+1)*dim + i] = src[i*dim + (j+1)];
                        dst[(j+2)*dim + i] = src[i*dim + (j+2)];
                        dst[(j+3)*dim + i] = src[i*dim + (j+3)];

                        dst[j*dim + i+1] = src[(i+1)*dim + j];
                        dst[(j+1)*dim + i+1] = src[(i+1)*dim + (j+1)];
                        dst[(j+2)*dim + i+1] = src[(i+1)*dim + (j+2)];
                        dst[(j+3)*dim + i+1] = src[(i+1)*dim + (j+3)];

                        dst[j*dim + i+2] = src[(i+2)*dim + j];
                        dst[(j+1)*dim + i+2] = src[(i+2)*dim + (j+1)];
                        dst[(j+2)*dim + i+2] = src[(i+2)*dim + (j+2)];
                        dst[(j+3)*dim + i+2] = src[(i+2)*dim + (j+3)];

                        dst[j*dim + i+3] = src[(i+3)*dim + j];
                        dst[(j+1)*dim + i+3] = src[(i+3)*dim + (j+1)];
                        dst[(j+2)*dim + i+3] = src[(i+3)*dim + (j+2)];
                        dst[(j+3)*dim + i+3] = src[(i+3)*dim + (j+3)];
                }
        }
        /* left out finishing off rest of elements as it is too tedious */
}

void test(void transpose(int *, int *, int), char *fn_name) {
        int *src = malloc(TEST_DIM*TEST_DIM*sizeof(int));
        int *dst = malloc(TEST_DIM*TEST_DIM*sizeof(int));
        int *exp = malloc(TEST_DIM*TEST_DIM*sizeof(int));
        int i;

        for (i = 0; i < TEST_DIM*TEST_DIM; i++)
                src[i] = i;
        transpose0(exp, src, TEST_DIM);
        transpose(dst, src, TEST_DIM);
        for (i = 0; i < TEST_DIM*TEST_DIM; i++)
                if (exp[i] != dst[i]) {
                        printf("%s fail at i=%d, expected: %d, actual: %d\n",
                               fn_name, i, exp[i], dst[i]);
                        assert(0);
                }
        free(src);
        free(dst);
        free(exp);
}

void test_perf(void transpose(int *, int *, int), char *fn_name, int dim) {
        clock_t start, end;
        int *src = malloc(dim*dim*sizeof(int));
        int *dst = malloc(dim*dim*sizeof(int));

        start = clock();
        transpose(dst, src, dim);
        end = clock();
        printf("transpose %dx%d matrix, %s: %.2f sec\n",
               dim, dim, fn_name, ((double) (end - start)) / CLOCKS_PER_SEC);
        free(src);
        free(dst);
}

int main() {
        test(transpose1, "transpose1");
        test(transpose2, "transpose2");
        test(transpose3, "transpose3");
        test(transpose4, "transpose4");

        test_perf(transpose0, TRANSPOSE0_NAME, N_SMALL);
        test_perf(transpose1, TRANSPOSE1_NAME, N_SMALL);
        test_perf(transpose2, TRANSPOSE2_NAME, N_SMALL);
        test_perf(transpose3, TRANSPOSE3_NAME, N_SMALL);
        test_perf(transpose4, TRANSPOSE4_NAME, N_SMALL);
        printf("\n");
        test_perf(transpose0, TRANSPOSE0_NAME, N_MEDIUM);
        test_perf(transpose1, TRANSPOSE1_NAME, N_MEDIUM);
        test_perf(transpose2, TRANSPOSE2_NAME, N_MEDIUM);
        test_perf(transpose3, TRANSPOSE3_NAME, N_MEDIUM);
        test_perf(transpose4, TRANSPOSE4_NAME, N_MEDIUM);
        printf("\n");
        test_perf(transpose0, TRANSPOSE0_NAME, N_LARGE);
        test_perf(transpose1, TRANSPOSE1_NAME, N_LARGE);
        test_perf(transpose2, TRANSPOSE2_NAME, N_LARGE);
        test_perf(transpose3, TRANSPOSE3_NAME, N_LARGE);
        test_perf(transpose4, TRANSPOSE4_NAME, N_LARGE);
}
