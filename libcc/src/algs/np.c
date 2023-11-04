#include "cc_algs_np.h"

int nqueens_solve(int n, int *board, int i, int *out);
int nqueens_is_safe(int n, int *board, int i, int j);

int nqueens(int n, int *out) {
  int board[n][n], i, j;

  for (i = 0; i < n; i++) {
    for (j = 0; j < n; j++) {
      board[i][j] = 0;
    }
  }
  return nqueens_solve(n, (int *)board, 0, out);
}

int nqueens_solve(int n, int *board, int i, int *out) {
  int j;

  if (i == n) {
    return 1;
  }
  for (j = 0; j < n; j++) {
    if (nqueens_is_safe(n, board, i, j)) {
      out[i] = j;
      board[i * n + j] = 1;
      if (nqueens_solve(n, board, i + 1, out)) {
        return 1;
      }
      board[i * n + j] = 0;
    }
  }
  return 0;
}

int nqueens_is_safe(int n, int *board, int i, int j) {
  int k, l;

  /* Check columns */
  for (k = 0; k < i; k++) {
    if (board[k * n + j]) {
      return 0;
    }
  }
  /* Check diagonals */
  k = i - 1;
  l = j - 1;
  while (k >= 0 && l >= 0) {
    if (board[k * n + l]) {
      return 0;
    }
    k--;
    l--;
  }
  k = i - 1;
  l = j + 1;
  while (k >= 0 && l < n) {
    if (board[k * n + l]) {
      return 0;
    }
    k--;
    l++;
  }
  return 1;
}

int subset_sum(int sum, int *a, int n, int *out) {
  int i;

  for (i = 0; i < n; i++) {
    out[i] = 0;
  }
  return subset_sum_solve(sum, a, n, out, 0, 0);
}

int subset_sum_solve(int sum, int *a, int n, int *out, int i, int acc) {
  if (acc == sum) {
    return 1;
  }
  if (i == n || acc + a[i] > sum) {
    return 0;
  }
  out[i] = 1;
  if (subset_sum_solve(sum, a, n, out, i + 1, acc + a[i])) {
    return 1;
  }
  out[i] = 0;
  return subset_sum_solve(sum, a, n, out, i + 1, acc);
}
