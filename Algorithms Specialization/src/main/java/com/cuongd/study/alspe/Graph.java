package com.cuongd.study.alspe;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public final int V;
    private int E;
    private final List<Edge>[] adj;

    public Graph(int V) {
        this.V = V;
        adj = new List[V];
        for (int v = 0; v < this.V; v++) {
            adj[v] = new ArrayList<>();
        }
    }

    public void addEdge(Edge e) {
        adj[e.v].add(e);
        adj[e.w].add(e);
        E++;
    }

    public int E() {
        return E;
    }

    public List<Edge> adj(int v) {
        return adj[v];
    }

    public static class Edge {
        public final int v;
        public final int w;
        public final double weight;

        public Edge(int v, int w) {
            this.v = v;
            this.w = w;
            this.weight = 0;
        }

        public Edge(int v, int w, double weight) {
            this.v = v;
            this.w = w;
            this.weight = weight;
        }

        public int other(int v) {
            if (v == this.v) return this.w;
            if (v == this.w) return this.v;
            throw new IllegalArgumentException();
        }
    }
}
