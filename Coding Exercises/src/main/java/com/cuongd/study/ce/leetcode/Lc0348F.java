package com.cuongd.study.ce.leetcode;

class Lc0348F {
  private static class TicTacToe {
    private int n;
    private int[][] p1;
    private int[] p1d;
    private int[][] p2;
    private int[] p2d;

    public TicTacToe(int n) {
      this.n = n;
      p1 = new int[2][n];
      p2 = new int[2][n];
      p1d = new int[2];
      p2d = new int[2];
      fill(p1, p1d);
      fill(p2, p2d);
    }

    public int move(int row, int col, int player) {
      int[][] p = player == 1 ? p1 : p2;
      int[] pd = player == 1 ? p1d : p2d;
      p[0][row]++;
      if (p[0][row] == n) return player;
      p[1][col]++;
      if (p[1][col] == n) return player;
      if (row == col) {
        pd[0]++;
        if (pd[0] == n) return player;
      }
      if (row == (n - 1) - col) {
        pd[1]++;
        if (pd[1] == n) return player;
      }
      return 0;
    }

    private void fill(int[][] a, int[] d) {
      for (int i = 0; i < 2; i++) {
        for (int j = 0; j < n; j++) {
          a[i][j] = 0;
        }
      }
      d[0] = 0;
      d[1] = 0;
    }
  }

  public static void main(String[] args) {
    TicTacToe game = new TicTacToe(5);
    assert game.move(1, 2, 2) == 0;
    assert game.move(3, 0, 1) == 0;
    assert game.move(2, 2, 2) == 0;
    assert game.move(1, 0, 1) == 0;
    assert game.move(2, 1, 2) == 0;
    assert game.move(4, 4, 1) == 0;
    assert game.move(0, 3, 2) == 0;
    assert game.move(0, 0, 1) == 0;
    assert game.move(0, 1, 2) == 0;
    assert game.move(2, 3, 1) == 0;
    assert game.move(1, 1, 2) == 0;
    assert game.move(2, 4, 1) == 0;
    assert game.move(4, 0, 2) == 0;
    assert game.move(3, 4, 1) == 0;
    assert game.move(0, 2, 2) == 0;
    assert game.move(1, 3, 1) == 0;
    assert game.move(3, 1, 2) == 0;
    assert game.move(4, 1, 1) == 0;
    assert game.move(2, 0, 2) == 0;
    assert game.move(3, 3, 1) == 0;
    assert game.move(4, 2, 2) == 0;
    assert game.move(4, 3, 1) == 0;
    assert game.move(3, 2, 2) == 2;
  }
}
