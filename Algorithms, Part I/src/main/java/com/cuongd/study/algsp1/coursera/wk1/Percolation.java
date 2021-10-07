package com.cuongd.study.algsp1.coursera.wk1;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.HashSet;
import java.util.Set;

public class Percolation {
    private final int N;
    private final Site[][] grid;
    private final Set<Site> topSites;
    private final Set<Site> botSites;
    private int numOpenSites;
    private final WeightedQuickUnionUF uf;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        N = n;
        grid = new Site[n][n];
        topSites = new HashSet<>();
        botSites = new HashSet<>();
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

        grid[r][c].isOpen = true;
        if (r > 0) connectNeighbor(r, c, r - 1, c); // up
        if (r < N-1) connectNeighbor(r, c, r + 1, c); // down
        if (c > 0) connectNeighbor(r, c, r, c - 1); // left
        if (c < N-1) connectNeighbor(r, c, r, c + 1); // right
        if (r == 0) {
            topSites.add(grid[r][c]);
        } else if (r == N-1) {
            botSites.add(grid[r][c]);
        }
        numOpenSites++;
    }

    public boolean isOpen(int row, int col) {
        checkRowCol(row, col);
        return grid[row-1][col-1].isOpen;
    }

    public boolean isFull(int row, int col) {
        checkRowCol(row, col);
        int r = row - 1, c = col - 1;

        for (Site topSite : topSites) {
            int sr = topSite.row - 1;
            int sc = topSite.col - 1;
            if (uf.find(r*N + c) == uf.find(sr*N + sc)) {
                return true;
            }
        }
        return false;
    }

    public int numberOfOpenSites() {
        return numOpenSites;
    }

    public boolean percolates() {
        for (Site botSite : botSites) {
            if (isFull(botSite.row, botSite.col)) {
                return true;
            }
        }
        return false;
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
        boolean isFull;

        Site(int row, int col) {
            this.row = row;
            this.col = col;
            isOpen = false;
            isFull = false;
        }
    }
}
