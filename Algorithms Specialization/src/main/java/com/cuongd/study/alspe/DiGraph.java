package com.cuongd.study.alspe;

import java.util.ArrayList;
import java.util.List;

public class DiGraph {
    public final int V;
    private int E;
    private final List<DiEdge>[] adj;

    public DiGraph(int V) {
        this.V = V;
        adj = new List[V];
        for (int v = 0; v < this.V; v++) {
            adj[v] = new ArrayList<>();
        }
    }

    public void addEdge(DiEdge e) {
        adj[e.from].add(e);
    }

    public List<DiEdge> adj(int v) {
        return adj[v];
    }

    public static class DiEdge {
        public final int from;
        public final int to;

        public DiEdge(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }
}
