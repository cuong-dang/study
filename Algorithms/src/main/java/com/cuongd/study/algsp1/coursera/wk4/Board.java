package com.cuongd.study.algsp1.coursera.wk4;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int n;
    private final int[][] board;
    private int hamming = -1;
    private int manhattan = -1;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length;
        board = new int[n][n];
        for (int i = 0; i < n; ++i)
            //noinspection ManualArrayCopy
            for (int j = 0; j < n; ++j)
                board[i][j] = tiles[i][j];
    }

    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d\n", n));
        for (int i = 0; i < n; ++i) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < n; ++j)
                if (j != 0 && j != n - 1)
                    row.append(String.format("%d ", board[i][j]));
                else if (j == 0)
                    row.append(String.format(" %d ", board[i][j]));
                else
                    row.append(String.format("%d\n", board[i][j]));
            sb.append(row);
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        if (hamming != -1)
            return hamming;
        hamming = 0;
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
                if (board[i][j] != 0 && board[i][j] != i*n + j + 1)
                    ++hamming;
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        if (manhattan != -1)
            return manhattan;
        manhattan = 0;
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
                if (board[i][j] != 0)
                    if (board[i][j] != i*n + j + 1) {
                        int correctI = (board[i][j]-1) / n;
                        int correctJ = board[i][j] - correctI*n - 1;
                        manhattan += abs(i, correctI) + abs(j, correctJ);
                    }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        if (hamming == -1)
            hamming();
        return hamming == 0;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board that = (Board) o;
        if (this.board.length != that.board.length) return false;
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
                if (board[i][j] != that.board[i][j])
                    return false;
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new ArrayList<>();
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
                if (board[i][j] == 0) {
                    if (i - 1 >= 0)
                        neighbors.add(makeNeighbor(i, j, i-1, j));
                    if (i + 1 <= n - 1)
                        neighbors.add(makeNeighbor(i, j, i + 1, j));
                    if (j - 1 >= 0)
                        neighbors.add(makeNeighbor(i, j, i, j - 1));
                    if (j + 1 <= n - 1)
                        neighbors.add(makeNeighbor(i, j, i, j + 1));
                }
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        Board twin = new Board(board);
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
                if (j < n - 1 && board[i][j] != 0 && board[i][j+1] != 0) {
                    swap(twin.board, i, j, i, j+1);
                    return twin;
                } else if (j == n - 1 && i < n - 1 && board[i][j] != 0 && board[i+1][0] != 0) {
                    swap(twin.board, i, j, i+1, 0);
                    return twin;
                }
        throw new IllegalStateException();
    }

    private int abs(int a, int b) {
        return a > b ? a - b : b - a;
    }

    private Board makeNeighbor(int i, int j, int ii, int jj) {
        Board neighbor = new Board(board);
        neighbor.board[i][j] = neighbor.board[ii][jj];
        neighbor.board[ii][jj] = 0;
        return neighbor;
    }

    private static void swap(int[][] board, int i, int j, int ii, int jj) {
        int t = board[i][j];
        board[i][j] = board[ii][jj];
        board[ii][jj] = t;
    }
}
