package com.cuongd.study.algs.coursera.wk1;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final byte CLOSED = 0x0;
    private static final byte OPEN = 0x1;
    private static final byte TOP_CONNECTED = 0x2;
    private static final byte BOT_CONNECTED = 0x4;

    private final int n;
    private final byte[] grid;
    private int numOpenSites = 0;
    private boolean doesPercolate = false;
    private final WeightedQuickUnionUF uf;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        grid = new byte[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i*n + j] = CLOSED;
                if (i == 0) grid[i*n + j] |= TOP_CONNECTED;
                if (i == n - 1) grid[i*n +j] |= BOT_CONNECTED;
            }
        }
        uf = new WeightedQuickUnionUF(n * n);
    }

    public void open(int row, int col) {
        checkRowCol(row, col);
        int r = row - 1, c = col - 1, i = r*n + c, root;

        if ((grid[i] & OPEN) == OPEN) {
            return;
        }
        grid[i] |= OPEN;
        if (r > 0) connectNeighbor(r, c, r - 1, c); // up
        if (r < n - 1) connectNeighbor(r, c, r + 1, c); // down
        if (c > 0) connectNeighbor(r, c, r, c - 1); // left
        if (c < n - 1) connectNeighbor(r, c, r, c + 1); // right
        root = uf.find(i);
        doesPercolate = doesPercolate || ((grid[root] & TOP_CONNECTED) == TOP_CONNECTED) &&
                ((grid[root] & BOT_CONNECTED) == BOT_CONNECTED);
        numOpenSites++;
    }

    public boolean isOpen(int row, int col) {
        checkRowCol(row, col);
        int r = row - 1, c = col - 1;

        return (grid[r*n + c] & OPEN) == OPEN;
    }

    public boolean isFull(int row, int col) {
        checkRowCol(row, col);
        int r = row - 1, c = col - 1, root = uf.find(r*n + c);

        return isOpen(row, col) && ((grid[root] & TOP_CONNECTED) == TOP_CONNECTED);
    }

    public int numberOfOpenSites() {
        return numOpenSites;
    }

    public boolean percolates() {
        return doesPercolate;
    }

    private void checkRowCol(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException();
        }
    }

    private void connectNeighbor(int thisR, int thisC, int thatR, int thatC) {
        int root1, root2, unionBit;

        if ((grid[thatR * n + thatC] & OPEN) != OPEN) {
            return;
        }
        root1 = uf.find(thisR*n + thisC);
        root2 = uf.find(thatR*n + thatC);
        unionBit = grid[root1] | grid[root2];
        uf.union(root1, root2);
        assert (uf.find(root1) == uf.find(root2));
        assert (uf.find(thisR*n + thisC) == uf.find(root1));
        grid[uf.find(thisR*n + thisC)] = (byte) unionBit;
    }
}
