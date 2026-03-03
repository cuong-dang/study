package com.cuongd.study.ce.leetcode;

class Lc0348 {
  private static class TicTacToe {
    private int n;
    private int[][] b;

    public TicTacToe(int n) {
      this.n = n;
      b = new int[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          b[i][j] = 0;
        }
      }
    }

    public int move(int row, int col, int player) {
      b[row][col] = player;
      if (checkRow(player, row, col) == player) return player;
      if (checkCol(player, row, col) == player) return player;
      if (checkDiag(player, row, col) == player) return player;
      return 0;
    }

    private int checkRow(int player, int r, int c) {
      for (int col = 0; col < n; col++) {
        if (b[r][col] != player) return 0;
      }
      return player;
    }

    private int checkCol(int player, int r, int c) {
      for (int row = 0; row < n; row++) {
        if (b[row][c] != player) return 0;
      }
      return player;
    }

    private int checkDiag(int player, int r, int c) {
      boolean won = true;
      for (int i = 0; i < n; i++) {
        if (b[i][i] == player) continue;
        won = false;
        break;
      }
      if (won) return player;
      for (int i = 0; i < n; i++) {
        if (b[n - i - 1][i] != player) return 0;
      }
      return player;
    }
  }
}
