package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

public class Ex4425 {
    private double minDist;
    private Iterable<DirectedEdge> minPath;

    public Ex4425(EdgeWeightedDigraph G, Iterable<Integer> ss, Iterable<Integer> ts) {
        Ex4424 sp = new Ex4424(G, ss);
        minDist = Double.POSITIVE_INFINITY;
        for (int t : ts) {
            if (sp.distTo(t) < minDist) {
                minDist = sp.distTo(t);
                minPath = sp.pathTo(t);
            }
        }
    }

    public double minDist() { return minDist; }

    public Iterable<DirectedEdge> minPath() { return minPath; }
}
