package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.Set;

public class BFSWalk {
    private final Digraph G;
    private int[] distTo;
    private Queue<Integer> q;
    private int dist;

    public BFSWalk(Digraph G, int src) {
        this.G = G;
        distTo = new int[G.V()];
        Arrays.fill(distTo, -1);
        distTo[src] = 0;
        q = new Queue<>();
        q.enqueue(src);
    }

    public boolean done() {
        return q.isEmpty();
    }

    public void step(Set<Integer> landedOn) {
        Queue<Integer> nextQ = new Queue<>();
        while (!q.isEmpty()) {
            int v = q.dequeue();
            distTo[v] = dist;
            landedOn.add(v);
            for (int w : G.adj(v)) {
                nextQ.enqueue(w);
            }
        }
        q = nextQ;
        dist++;
    }

    public int distTo(int v) {
        return distTo[v];
    }
}
