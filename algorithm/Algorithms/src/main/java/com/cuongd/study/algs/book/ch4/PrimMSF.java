package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PrimMSF {
    private final Map<Integer, Iterable<Edge>> msf;
    private boolean[] marked;
    private Edge[] edgeTo;
    private double[] distTo;
    private IndexMinPQ<Double> pq;

    public PrimMSF(EdgeWeightedGraph G) {
        msf = new HashMap<>();
        marked = new boolean[G.V()];
        edgeTo = new Edge[G.V()];
        distTo = new double[G.V()];
        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
        pq = new IndexMinPQ<>(G.V());
        int count = 0;
        for (int i = 0; i < G.V(); i++) {
            if (!marked[i]) {
                msf.put(count++, mst(G, i));
            }
        }
    }

    public Map<Integer, Iterable<Edge>> msf() {
        return msf;
    }

    private Iterable<Edge> mst(EdgeWeightedGraph G, int s) {
        while (!pq.isEmpty()) {
            pq.delMin();
        }
        distTo[s] = 0;
        pq.insert(s, 0.0);
        while (!pq.isEmpty()) {
            visit(G, pq.delMin());
        }
        Bag<Edge> edges = new Bag<>();
        for (int v = 0; v < G.V(); v++) {
            if (distTo[v] != 0 && distTo[v] != Double.POSITIVE_INFINITY) {
                edges.add(edgeTo[v]);
                distTo[v] = Double.POSITIVE_INFINITY;
            }
        }
        return edges;
    }

    private void visit(EdgeWeightedGraph G, int v) {
        marked[v] = true;
        for (Edge e : G.adj(v)) {
            int w = e.other(v);
            if (marked[w]) {
                continue;
            }
            if (e.weight() < distTo[w]) {
                edgeTo[w] = e;
                distTo[w] = e.weight();
                if (pq.contains(w)) {
                    pq.changeKey(w, e.weight());
                } else {
                    pq.insert(w, e.weight());
                }
            }
        }
    }
}
