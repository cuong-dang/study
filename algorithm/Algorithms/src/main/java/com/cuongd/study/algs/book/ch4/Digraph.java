package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;

import java.util.HashMap;
import java.util.Map;

public class Digraph {
    private final int V;
    private int E;
    private final Bag<Integer>[] adj;

    public Digraph(int V) {
        this.V = V;
        this.E = 0;
        adj = (Bag<Integer>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<>();
        }
    }

    public Digraph(Digraph G) {
        this(G.V);
        for (int v = 0; v < G.V; v++) {
            for (int w : G.adj(v)) {
                addEdge(v, w);
            }
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public void addEdge(int v, int w) {
        adj[v].add(w);
        E++;
    }

    public boolean hasEdge(int v, int w) {
        for (int ww : adj(v)) {
            if (ww == w) {
                return true;
            }
        }
        return false;
    }

    public Iterable<Integer> adj(int v) {
        return adj[v];
    }

    public Digraph reverse() {
        Digraph R = new Digraph(V);

        for (int v = 0; v < V; v++) {
            for (int w : adj(v)) {
                R.addEdge(w, v);
            }
        }
        return R;
    }

    public boolean isTopologicalOrder(Iterable<Integer> order) {
        boolean[] marked = new boolean[V];
        Digraph reversed = reverse();

        for (int v : order) {
            marked[v] = true;
            for (int w : reversed.adj(v)) {
                if (!marked[w]) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isTopologicalOrder2(Iterable<Integer> order) {
        Map<Integer, Integer> vertexOrder = new HashMap<>();
        int i = 0;

        for (int v : order) {
            vertexOrder.put(v, i);
            i++;
        }
        for (int v = 0; v < V; v++) {
            for (int w : adj(v)) {
                if (vertexOrder.get(v) > vertexOrder.get(w)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Graph undirected() {
        Graph g = new Graph(V);
        for (int v = 0; v < V; v++) {
            for (int w : adj(v)) {
                g.addEdge(v, w);
            }
        }
        return g;
    }
}
