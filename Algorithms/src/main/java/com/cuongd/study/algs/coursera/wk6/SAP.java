package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

import java.util.List;

public class SAP {
    private final Digraph G;

    public SAP(Digraph G) {
        this.G = new Digraph(G);
    }

    public int length(int v, int w) {
        return findSAP(List.of(v), List.of(w), true);
    }

    public int ancestor(int v, int w) {
        return findSAP(List.of(v), List.of(w), false);
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return findSAP(v, w, true);
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return findSAP(v, w, false);
    }

    private int findSAP(Iterable<Integer> v, Iterable<Integer> w, boolean returningLength) {
        checkVertices(v);
        checkVertices(w);
        if (!v.iterator().hasNext() || !w.iterator().hasNext()) {
            return -1;
        }
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(this.G, w);

        int length = -1, ancestor = -1;
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                int d = bfsV.distTo(i) + bfsW.distTo(i);
                if (length == -1 || d < length) {
                    length = d;
                    ancestor = i;
                }
            }
        }
        return returningLength ? length : ancestor;
    }

    private void checkVertices(Iterable<Integer> x) {
        if (x == null) {
            throw new IllegalArgumentException();
        }
        x.forEach(i -> {
            if (i == null || i < 0 || i >= G.V()) {
                throw new IllegalArgumentException();
            }
        });
    }

    public static void main(String[] args) {
        // Intentionally empty.
    }
}
