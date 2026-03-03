package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ex4436 {
    private  EdgeWeightedDigraph G;
    private double[] distTo;
    private IndexMinPQ<Double> pq;

    public Ex4436(EdgeWeightedDigraph G) {
        this. G = G;
        distTo = new double[G.V()];
        pq = new IndexMinPQ<>(G.V());
    }

    public Iterable<Integer> nearest(int s, double d) {
        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
        distTo[s] = 0.0;
        pq.insert(s, 0.0);
        while (!pq.isEmpty()) {
            relax(G, pq.delMin(), d);
        }

        List<Integer> result = new ArrayList<>();
        for (int v = 0; v < G.V(); v++) {
            if (distTo[v] < Double.POSITIVE_INFINITY && v != s) {
                result.add(v);
            }
        }
        return result;
    }

    private void relax(EdgeWeightedDigraph G, int v, double d) {
        for (DirectedEdge e : G.adj(v)) {
            int w = e.to();
            if (distTo[w] > distTo[v] + e.weight() &&
                    !(distTo[v] + e.weight() > d)) {
                distTo[w] = distTo[v] + e.weight();
                if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
                else pq.insert(w, distTo[w]);
            }
        }
    }
}
