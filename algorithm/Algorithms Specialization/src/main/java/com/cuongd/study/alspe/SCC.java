package com.cuongd.study.alspe;


public class SCC {
    private final int[] id;
    private final boolean[] marked;
    private int count = 0;

    public SCC(DiGraph G) {
        id = new int[G.V];
        marked = new boolean[G.V];
        int[] topo = new Topological(G.reverse()).order();
        for (int i = 0; i < topo.length; i++) {
            if (!marked[topo[i]]) {
                dfs(G, topo[i]);
                count++;
            }
        }
    }

    private void dfs(DiGraph G, int v) {
        marked[v] = true;
        id[v] = count;
        for (DiGraph.DiEdge e : G.adj(v)) {
            if (!marked[e.to]) {
                dfs(G, e.to);
            }
        }
    }

    public int[] id() {
        return id;
    }
}
