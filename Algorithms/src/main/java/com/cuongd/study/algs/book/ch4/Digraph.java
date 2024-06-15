package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;

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
}
