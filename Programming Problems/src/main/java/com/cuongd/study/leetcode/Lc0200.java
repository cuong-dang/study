package com.cuongd.study.leetcode;

class Lc0200 {
    char[][] grid;
    boolean[][] marked;

    public int numIslands(char[][] grid) {
        this.grid = grid;
        marked = new boolean[grid.length][grid[0].length];
        int count = 0;
        for (int i = 0; i < marked.length; i++) {
            for (int j = 0; j < marked[i].length; j++) {
                marked[i][j] = false;
            }
        }
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '1' && !marked[i][j]) {
                    count++;
                    dfs(i, j);
                }
            }
        }
        return count;
    }

    private void dfs(int i, int j) {
        marked[i][j] = true;
        int[] iDeltas = new int[]{-1, 0, 0, 1};
        int[] jDeltas = new int[]{0, -1, 1, 0};
        for (int k = 0; k < 4; k++) {
            int u = i + iDeltas[k];
            int v = j + jDeltas[k];
            if (0 <= u && u < grid.length && 0 <= v && v < grid[u].length &&
                    grid[u][v] == '1' && !marked[u][v]) {
                dfs(u, v);
            }
        }
    }
}
