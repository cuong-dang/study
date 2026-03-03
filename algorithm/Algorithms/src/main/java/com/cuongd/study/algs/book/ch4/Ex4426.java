package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ex4426 {
    private double[] distTo;
    private Integer[] pathTo;
    private boolean[] marked;
    private int s;

    public Ex4426(AdjEdgeWeightedGraph G, int s) {
        distTo = new double[G.V()];
        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
        distTo[s] = 0.0;
        pathTo = new Integer[G.V()];
        Arrays.fill(pathTo, null);
        marked = new boolean[G.V()];
        this.s = s;
        for (int i = 0; i < G.V(); i++) {
            relax(G, nextVertex());
        }
    }

    public boolean hasPathTo(int v) {
        return distTo[v] != Double.POSITIVE_INFINITY;
    }

    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<Integer> stack = new Stack<>();
        for (int w = v; pathTo[w] != null; w = pathTo[w]) {
            stack.push(w);
        }
        stack.push(s);
        List<Integer> result = new ArrayList<>();
        stack.forEach(result::add);
        return result;
    }

    private int nextVertex() {
        int v = -1;
        double minDist = Double.POSITIVE_INFINITY;

        for (int i = 0; i < distTo.length; i++) {
            if (!marked[i] && distTo[i] < minDist) {
                v = i;
                minDist = distTo[i];
            }
        }
        marked[v] = true;
        return v;
    }

    private void relax(AdjEdgeWeightedGraph G, int v) {
        for (int w : G.adj(v)) {
            if (distTo[w] > distTo[v] + G.weight(v, w)) {
                distTo[w] = distTo[v] + G.weight(v, w);
                pathTo[w] = v;
            }
        }
    }
}
