package com.cuongd.study.alspe;

import java.util.ArrayList;
import java.util.List;

public class DiGraph {
    public final int V;
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

    public DiGraph reverse() {
        DiGraph GRev = new DiGraph(V);
        for (int v = 0; v < V; v++) {
            for (DiEdge e : adj(v)) {
                GRev.addEdge(new DiEdge(e.to, e.from));
            }
        }
        return GRev;
    }

    public static class DiEdge {
        public final int from;
        public final int to;
        public final double weight;

        public DiEdge(int from, int to) {
            this.from = from;
            this.to = to;
            this.weight = 0;
        }

        public DiEdge(int from, int to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
}
