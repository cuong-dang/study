package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;

public class Graph {
    private final int V;
    private int E;
    private final Bag<Integer>[] adj;

    public Graph(int V) {
        this.V = V;
        this.E = 0;
        adj = (Bag<Integer>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<>();
        }
    }

    public Graph(Graph G) {
        V = G.V;
        E = G.E;
        adj = (Bag<Integer>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<>();
            for (int w : G.adj[v]) {
                adj[v].add(w);
            }
        }
    }

    public Graph(In in) {
        this(in.readInt());
        int E = in.readInt(), i = 0;
        do {
            int v = in.readInt();
            String[] adjList = Arrays
                    .stream(in.readLine().split(" "))
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            for (String w : adjList) {
                addEdge(v, Integer.parseInt(w));
                i++;
            }
        } while (i != E);
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public void addEdge(int v, int w) {
        if (v == w) {
            throw new IllegalArgumentException("Self-loops are disallowed");
        }
        if (hasEdge(v, w)) {
            throw new IllegalArgumentException("Parallel edges are disallowed");
        }
        adj[v].add(w);
        adj[w].add(v);
        E++;
    }

    public Iterable<Integer> adj(int v) {
        return adj[v];
    }

    public boolean hasEdge(int v, int w) {
        for (int ww : adj(v)) {
            if (ww == w) {
                return true;
            }
        }
        return false;
    }
}
