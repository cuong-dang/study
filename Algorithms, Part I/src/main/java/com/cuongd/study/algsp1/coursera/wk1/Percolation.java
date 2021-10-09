package com.cuongd.study.algsp1.coursera.wk1;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final byte UNINITIALIZED = -1;

    private final int n;
    private int topRoot = UNINITIALIZED;
    private int botRoot = UNINITIALIZED;
    private final byte[][] grid;
    private int numOpenSites;
    private final WeightedQuickUnionUF uf;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        grid = new byte[n][n];
        numOpenSites = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = 0;
            }
        }
        uf = new WeightedQuickUnionUF(n * n);
    }

    public void open(int row, int col) {
        checkRowCol(row, col);
        int r = row - 1, c = col - 1;

        if (grid[r][c] == 1) {
            return;
        }
        grid[r][c] = 1;
        if (r == 0) {
            if (topRoot == UNINITIALIZED) {
                topRoot = r*n + c;
            } else {
                uf.union(topRoot, r*n + c);
            }
        } else if (r == n - 1) {
            if (botRoot == UNINITIALIZED) {
                botRoot = r*n + c;
            } else {
                uf.union(botRoot, r*n + c);
            }
        }
        if (r > 0) connectNeighbor(r, c, r - 1, c); // up
        if (r < n - 1) connectNeighbor(r, c, r + 1, c); // down
        if (c > 0) connectNeighbor(r, c, r, c - 1); // left
        if (c < n - 1) connectNeighbor(r, c, r, c + 1); // right
        numOpenSites++;
    }

    public boolean isOpen(int row, int col) {
        checkRowCol(row, col);
        return grid[row-1][col-1] == 1;
    }

    public boolean isFull(int row, int col) {
        checkRowCol(row, col);
        int r = row - 1, c = col - 1;

        return topRoot != UNINITIALIZED && uf.find(r*n + c) == uf.find(topRoot);
    }

    public int numberOfOpenSites() {
        return numOpenSites;
    }

    public boolean percolates() {
        return (n == 1 && topRoot != UNINITIALIZED) ||
                (topRoot != UNINITIALIZED && botRoot != UNINITIALIZED &&
                        uf.find(topRoot) == uf.find(botRoot));
    }

    private void checkRowCol(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException();
        }
    }

    private void connectNeighbor(int thisR, int thisC, int thatR, int thatC) {
        if (grid[thatR][thatC] == 1) uf.union(thisR*n + thisC, thatR*n + thatC);
    }
}
