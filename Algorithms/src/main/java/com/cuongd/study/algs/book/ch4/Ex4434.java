package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.Arrays;

public class Ex4434 {
    private double[] distTo;
    private DirectedEdge[] edgeTo;
    private IndexMinPQ<Double> pq;

    public Ex4434(EdgeWeightedDigraph G, int s) {
        distTo = new double[G.V()];
        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
        edgeTo = new DirectedEdge[G.V()];
        pq = new IndexMinPQ<>(G.V());

        distTo[s] = 0.0;
        pq.insert(0, 0.0);
        while (!pq.isEmpty()) relax(G, pq.delMin());
    }

    private void relax(EdgeWeightedDigraph G, int v) {
        for (DirectedEdge e : G.adj(v)) {
            int w = e.to();
            if (distTo[w] > distTo[v] + e.weight() &&
                    (edgeTo[v] == null || edgeTo[v].weight() > e.weight())) {
                distTo[w] = distTo[v] + e.weight();
                edgeTo[w] = e;
            }
        }
    }
}
