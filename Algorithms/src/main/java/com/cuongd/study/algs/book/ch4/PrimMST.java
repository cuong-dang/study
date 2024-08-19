package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.Arrays;

public class PrimMST {
    private Edge[] edgeTo;
    private double[] distTo;
    private boolean[] marked;
    private IndexMinPQ<Double> pq;

    public PrimMST(EdgeWeightedGraph G) {
        edgeTo = new Edge[G.V()];
        distTo = new double[G.V()];
        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
        marked = new boolean[G.V()];
        pq = new IndexMinPQ<>(G.V());

        distTo[0] = 0.0;
        pq.insert(0, 0.0);
        while (!pq.isEmpty()) {
            visit(G, pq.delMin());
        }
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

    public Iterable<Edge> edges() {
        Bag<Edge> mst = new Bag<>();
        for (int i = 1; i < edgeTo.length; i++) {
            mst.add(edgeTo[i]);
        }
        return mst;
    }
}
