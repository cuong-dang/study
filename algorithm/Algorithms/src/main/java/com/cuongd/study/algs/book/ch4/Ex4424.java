package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

import java.util.ArrayList;
import java.util.List;

public class Ex4424 {
    private DijkstraSP sp;

    public Ex4424(EdgeWeightedDigraph G, Iterable<Integer> ss) {
        EdgeWeightedDigraph G_ = new EdgeWeightedDigraph(G.V() + 1);
        for (int v = 0; v < G.V(); v++) {
            for (DirectedEdge e : G.adj(v)) {
                G_.addEdge(e);
            }
        }
        for (int s : ss) {
            G_.addEdge(new DirectedEdge(G.V(), s, 0.0));
        }
        sp = new DijkstraSP(G_, G.V());
    }

    public boolean hasPathTo(int v) {
        return sp.hasPathTo(v);
    }

    public double distTo(int v) {
        return sp.distTo(v);
    }

    public Iterable<DirectedEdge> pathTo(int v) {
        List<DirectedEdge> result = new ArrayList<>();
        boolean skippingFirst = true;
        for (DirectedEdge e : sp.pathTo(v)) {
            if (skippingFirst) {
                skippingFirst = false;
                continue;
            }
            result.add(e);
        }
        return result;
    }
}
