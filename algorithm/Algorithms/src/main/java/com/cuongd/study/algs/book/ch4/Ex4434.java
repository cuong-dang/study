package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ex4434 {
    private double[] distToDec;
    private DirectedEdge[] edgeToDec;
    private double[] distToInc;
    private DirectedEdge[] edgeToInc;
    private IndexMinPQ<Double> pq;

    public Ex4434(EdgeWeightedDigraph G, int s) {
        distToDec = new double[G.V()];
        Arrays.fill(distToDec, Double.POSITIVE_INFINITY);
        edgeToDec = new DirectedEdge[G.V()];
        pq = new IndexMinPQ<>(G.V());

        distToDec[s] = 0.0;
        pq.insert(0, 0.0);
        while (!pq.isEmpty())
            relaxDec(G, pq.delMin());

        distToInc = new double[G.V()];
        Arrays.fill(distToInc, Double.POSITIVE_INFINITY);
        edgeToInc = new DirectedEdge[G.V()];

        distToInc[s] = 0.0;
        pq.insert(0, 0.0);
        while (!pq.isEmpty())
            relaxInc(G, pq.delMin());
    }

    private void relaxDec(EdgeWeightedDigraph G, int v) {
        for (DirectedEdge e : G.adj(v)) {
            int w = e.to();
            if (distToDec[w] > distToDec[v] + e.weight() &&
                    (edgeToDec[v] == null || edgeToDec[v].weight() > e.weight())) {
                distToDec[w] = distToDec[v] + e.weight();
                edgeToDec[w] = e;
                if (pq.contains(w)) pq.decreaseKey(w, distToDec[w]);
                else pq.insert(w, distToDec[w]);
            }
        }
    }

    private void relaxInc(EdgeWeightedDigraph G, int v) {
        for (DirectedEdge e : G.adj(v)) {
            int w = e.to();
            if (distToInc[w] > distToInc[v] + e.weight() &&
                    (edgeToInc[v] == null || edgeToInc[v].weight() < e.weight())) {
                distToInc[w] = distToInc[v] + e.weight();
                edgeToInc[w] = e;
                if (pq.contains(w)) pq.decreaseKey(w, distToInc[w]);
                else pq.insert(w, distToInc[w]);
            }
        }
    }

    public Iterable<DirectedEdge> pathTo(int v) {
        if (distToDec[v] == Double.POSITIVE_INFINITY &&
                distToInc[v] == Double.POSITIVE_INFINITY) return null;
        DirectedEdge[] edgeTo;
        if (distToDec[v] == Double.POSITIVE_INFINITY) {
            edgeTo = edgeToInc;
        } else if (distToInc[v] == Double.POSITIVE_INFINITY) {
            edgeTo = edgeToDec;
        } else {
            edgeTo = distToDec[v] < distToInc[v] ? edgeToDec : edgeToInc;
        }
        Stack<DirectedEdge> stack = new Stack<>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            stack.push(e);
        }
        List<DirectedEdge> result = new ArrayList<>();
        stack.forEach(result::add);
        return result;
    }
}
