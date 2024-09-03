package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ex447 {
    private DirectedEdge[] edgeTo;
    private DirectedEdge[] edgeTo2;
    private double[] distTo;
    private double[] distTo2;
    private boolean[] diverted;
    private IndexMinPQ<Double> pq;

    public Ex447(EdgeWeightedDigraph G, int s) {
        edgeTo = new DirectedEdge[G.V()];
        edgeTo2 = new DirectedEdge[G.V()];
        distTo = new double[G.V()];
        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
        distTo2 = new double[G.V()];
        Arrays.fill(distTo2, Double.POSITIVE_INFINITY);
        diverted = new boolean[G.V()];
        pq = new IndexMinPQ<>(G.V());
        distTo[s] = 0.0;
        distTo2[s] = 0.0;
        pq.insert(s, 0.0);
        while (!pq.isEmpty()) {
            relax(G, pq.delMin());
        }
    }

    public boolean has2ndShortest(int v) {
        return diverted[v];
    }

    public List<DirectedEdge> secondShortest(int v) {
        if (!diverted[v]) return null;
        Stack<DirectedEdge> stack = new Stack<>();
        for (DirectedEdge e = edgeTo2[v]; e != null; e = edgeTo2[e.from()]) {
            stack.push(e);
        }
        List<DirectedEdge> result = new ArrayList<>();
        stack.forEach(result::add);
        return result;
    }

    private void relax(EdgeWeightedDigraph G, int v) {
        for (DirectedEdge e : G.adj(v)) {
            int w = e.to();
            if (distTo[w] > distTo[v] + e.weight()) {
                distTo[w] = distTo[v] + e.weight();
                edgeTo[w] = e;
                if (pq.contains(w)) pq.changeKey(w, distTo[w]);
                else pq.insert(w, distTo[w]);
                if (!diverted[w]) { // follow along
                    distTo2[w] = distTo[w];
                    edgeTo2[w] = e;
                }
            } else if (!diverted[w]) { // first divergence
                distTo2[w] = distTo[v] + e.weight();
                edgeTo2[w] = e;
                diverted[w] = true;
            }
            if (diverted[w] && distTo2[w] > distTo2[w] + e.weight()) {  // normal relaxation
                distTo2[w] = distTo2[v] + e.weight();
                edgeTo2[w] = e;
            }
            if (e != edgeTo[w] && distTo2[w] > distTo[v] + e.weight()) {
                for (DirectedEdge et = edgeTo[v]; et != null; et = edgeTo[e.from()]) {
                    int vt = et.to();
                    edgeTo2[vt] = et;
                    distTo2[vt] = distTo[vt];
                }
                distTo2[w] = distTo[v] + e.weight();
                edgeTo2[w] = e;
                diverted[w] = true;
            }
        }
    }
}
