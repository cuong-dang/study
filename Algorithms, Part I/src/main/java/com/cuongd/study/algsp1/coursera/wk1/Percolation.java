package com.cuongd.study.algsp1.coursera.wk1;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final int UNINITIALIZED = -1;

    private final int N;
    private int topRoot = UNINITIALIZED;
    private int botRoot = UNINITIALIZED;
    private final Site[][] grid;
    private int numOpenSites;
    private final WeightedQuickUnionUF uf;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        N = n;;
        grid = new Site[n][n];
        numOpenSites = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = new Site(i+1, j+1);
            }
        }
        uf = new WeightedQuickUnionUF(n * n);
    }

    public void open(int row, int col) {
        checkRowCol(row, col);
        int r = row - 1, c = col - 1;

        if (grid[r][c].isOpen) {
            return;
        }
        grid[r][c].isOpen = true;
        if (r == 0) {
            if (topRoot == UNINITIALIZED) {
                topRoot = r*N + c;
            } else {
                uf.union(topRoot, r * N + c);
            }
        } else if (r == N-1) {
            if (botRoot == UNINITIALIZED) {
                botRoot = r*N + c;
            } else {
                uf.union(botRoot, r*N + c);
            }
        }
        if (r > 0) connectNeighbor(r, c, r - 1, c); // up
        if (r < N-1) connectNeighbor(r, c, r + 1, c); // down
        if (c > 0) connectNeighbor(r, c, r, c - 1); // left
        if (c < N-1) connectNeighbor(r, c, r, c + 1); // right
        numOpenSites++;
    }

    public boolean isOpen(int row, int col) {
        checkRowCol(row, col);
        return grid[row-1][col-1].isOpen;
    }

    public boolean isFull(int row, int col) {
        checkRowCol(row, col);
        int r = row - 1, c = col - 1;

        return topRoot != UNINITIALIZED && uf.find(r*N + c) == uf.find(topRoot);
    }

    public int numberOfOpenSites() {
        return numOpenSites;
    }

    public boolean percolates() {
        return topRoot != UNINITIALIZED && botRoot != UNINITIALIZED &&
                uf.find(topRoot) == uf.find(botRoot);
    }

    private void checkRowCol(int row, int col) {
        if (row < 1 || row > N || col < 1 || col > N ) {
            throw new IllegalArgumentException();
        }
    }

    private void connectNeighbor(int thisR, int thisC, int thatR, int thatC) {
        if (grid[thatR][thatC].isOpen) uf.union(thisR*N + thisC, thatR*N + thatC);
    }

    private static class Site {
        int row, col;
        boolean isOpen;

        Site(int row, int col) {
            this.row = row;
            this.col = col;
            isOpen = false;
        }
    }
}
