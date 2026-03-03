package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

public class Ex4423 {
    private boolean hasPath;
    private double distTo;
    private Iterable<DirectedEdge> path;

    public Ex4423(EdgeWeightedDigraph G, int s, int t) {
        DijkstraSP sp = new DijkstraSP(G, s);
        hasPath = sp.hasPathTo(t);
        distTo = sp.distTo(t);
        path = sp.pathTo(t);
    }

    public boolean hasPath() {
        return hasPath;
    }

    public double distTo() {
        return distTo;
    }

    public Iterable<DirectedEdge> path() {
        return path;
    }
}
