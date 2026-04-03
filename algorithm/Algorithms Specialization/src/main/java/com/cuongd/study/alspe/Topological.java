package com.cuongd.study.alspe;

public class Topological {
    private final int[] id;
    private final boolean[] marked;
    private int count;

    public Topological(DiGraph G) {
        id = new int[G.V];
        marked = new boolean[G.V];
        count = G.V - 1;
        for (int v = 0; v < G.V; v++) {
            if (!marked[v]) {
                dfs(G, v);
            }
        }
    }

    private void dfs(DiGraph G, int v) {
        marked[v] = true;
        for (DiGraph.DiEdge e : G.adj(v)) {
            if (!marked[e.to]) {
                dfs(G, e.to);
            }
        }
        id[v] = count--;
    }

    public int[] order() {
        int[] r = new int[id.length];
        for (int i = 0; i < id.length; i++) {
            r[id[i]] = i;
        }
        return r;
    }
}
