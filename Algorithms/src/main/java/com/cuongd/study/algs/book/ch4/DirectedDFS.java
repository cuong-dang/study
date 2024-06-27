package com.cuongd.study.algs.book.ch4;

import java.util.Arrays;

public class DirectedDFS {
    private final boolean[] marked;
    private final int[] length;

    public DirectedDFS(Digraph G, int s) {
        marked = new boolean[G.V()];
        length = new int[G.V()];
        Arrays.fill(length, -1);
        dfs(G, s);
    }

    public DirectedDFS(Digraph G, Iterable<Integer> sources) {
        marked = new boolean[G.V()];
        length = new int[G.V()];
        Arrays.fill(length, -1);
        for (int s : sources) {
            if (!marked[s]) {
                dfs(G, s);
            }
        }
    }

    private void dfs(Digraph G, int v) {
        marked[v] = true;
        if (length[v] == -1) {
            length[v] = 0;
        }
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                length[w] = length[v] + 1;
                dfs(G, w);
            }
        }
    }

    public boolean marked(int v) {
        return marked[v];
    }

    public int length(int v) {
        return length[v];
    }
}
