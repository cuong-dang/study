package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EdgeWeightedGraph {
    private final int V;
    private int E;
    private final Bag<Edge>[] adj;

    public EdgeWeightedGraph(int V) {
        this.V = V;
        this.E = 0;
        adj = (Bag<Edge>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<>();
        }
    }

    public EdgeWeightedGraph(In in) {
        this(in.readInt());
        int E = in.readInt(), i = 0;
        do {
            int v = in.readInt(), w = in.readInt();
            double weight = in.readDouble();
            Edge e = new Edge(v, w, weight);
            adj[v].add(e);
            adj[w].add(e);
            i++;
        } while (i != E);
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public void addEdge(Edge e) {
        int v = e.either(), w = e.other(v);
        adj[v].add(e);
        adj[w].add(e);
        E++;
    }

    public Iterable<Edge> adj(int v) {
        return adj[v];
    }

    public Iterable<Edge> edges() {
        Bag<Edge> b = new Bag<>();
        for (int v = 0; v < V; v++) {
            for (Edge e : adj[v]) {
                if (e.other(v) > v) {
                    b.add(e);
                }
            }
        }
        return b;
    }

    public Iterable<Integer> cycle() {
        int[] edgeTo = new int[V];
        Arrays.fill(edgeTo, -1);
        boolean[] marked = new boolean[V];
        Arrays.fill(marked, false);
        List<Integer> cycleStartEnd = new ArrayList<>();
        cycleStartEnd.add(null);
        cycleStartEnd.add(null);
        for (int i = 0; i < V; i++) {
            if (!marked[i]) {
                dfs(i, i, marked, edgeTo, cycleStartEnd);
            }
            if (cycleStartEnd.get(0) != null) {
                break;
            }
        }
        Stack<Integer> ans = new Stack<>();
        if (cycleStartEnd.get(0) == null) {
            return ans;
        }
        int cycleStart = cycleStartEnd.get(0), cycleEnd = cycleStartEnd.get(1);
        ans.push(cycleEnd);
        while (cycleStart != cycleEnd) {
            cycleEnd = edgeTo[cycleEnd];
            ans.push(cycleEnd);
        }
        return ans;
    }

    private void dfs(int v, int w, boolean[] marked, int[] edgeTo, List<Integer> result) {
        marked[v] = true;
        for (Edge e : adj(v)) {
            if (result.get(0) != null) {
                return;
            }
            int x = e.other(v);
            if (!marked[x]) {
                edgeTo[x] = v;
                dfs(x, v, marked, edgeTo, result);
            } else if (x != w) {
                result.set(0, x);
                result.set(1, v);
            }
        }
    }
}
