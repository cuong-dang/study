package com.cuongd.study.algs.book.ch4;

public class Cycle {
    private boolean[] marked;
    private boolean hasCycle;
    private int[] edgeTo;

    public Cycle(Graph G) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        for (int s = 0; s < G.V(); s++) {
            if (!marked[s]) {
                dfs(G, s, s);
            }
        }
    }

    private void dfs(Graph G, int v, int u) {
        marked[v] = true;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                edgeTo[w] = v;
                dfs(G, w, v);
            } else if (w != u && // not the immediately previous vertex
                    edgeTo[w] != v && // parallel edges
                    w != v) { // self-loops
                hasCycle = true;
            }
        }
    }

    public boolean hasCycle() {
        return hasCycle;
    }
}
