package com.cuongd.study.leetcode;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class Lc0317 {
    private int[][] g;
    private int numHouses;

    public int shortestDistance(int[][] grid) {
        this.g = grid;
        // count num houses
        numHouses = 0;
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[0].length; j++) {
                if (g[i][j] == 1) {
                    numHouses++;
                }
            }
        }
        // main
        int ans = Integer.MAX_VALUE;
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[0].length; j++) {
                if (g[i][j] != 0) continue;
                ans = Math.min(ans, bfs(i, j));
            }
        }
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    private int bfs(int i, int j) {
        int reached = 0;
        boolean[][] marked = new boolean[g.length][g[0].length];
        int[][] deltas = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        Queue<List<Integer>> q = new LinkedList<>();
        q.add(List.of(i, j, 0));
        marked[i][j] = true;
        int steps = 0;
        while (!q.isEmpty()) {
            if (reached == numHouses) return steps;
            List<Integer> pos = q.remove();
            int x = pos.get(0), y = pos.get(1), s = pos.get(2);
            for (int[] delta : deltas) {
                int nx = x + delta[0], ny = y + delta[1];
                if (nx < 0 || nx >= g.length || ny < 0 || ny >= g[0].length || marked[nx][ny])
                    continue;
                marked[nx][ny] = true;
                if (g[nx][ny] == 0) {
                    q.add(List.of(nx, ny, s + 1));
                    continue;
                }
                if (g[nx][ny] == 1) {
                    reached++;
                    steps += s + 1;
                }
            }
        }
        if (reached != numHouses) {
            for (int m = 0; m < g.length; m++) {
                for (int n = 0; n < g[0].length; n++) {
                    if (marked[m][n] && g[m][n] == 0) {
                        g[m][n] = 2;
                    }
                }
            }
            return Integer.MAX_VALUE;
        }
        return steps;
    }

    public static void main(String[] args) {
        new Lc0317().shortestDistance(new int[][]{{1, 0}});
    }
}
