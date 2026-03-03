package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.AcyclicSP;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

public class Ex4431 {
    private double[] distTo;

    public Ex4431(EdgeWeightedDigraph G) {
        distTo = new double[G.V()];
        AcyclicSP sp = new AcyclicSP(G, 0);
        for (int i = 1; i < G.V(); i++) {
            distTo[i] = sp.distTo(i);
        }
    }

    public double distTo(int v, int w) {
        if (v == 0) return distTo[w];
        if (v > w) return distTo(w, v);
        return distTo[w] - distTo[v];
    }
}
