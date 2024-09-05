package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.List;

/** Reference: <a href="https://en.wikipedia.org/wiki/K_shortest_path_routing">k shortest path routing</a> */
public class Ex447 {
    private List<DirectedPath>[] paths;
    private MinPQ<DirectedPath> pq;

    public Ex447(EdgeWeightedDigraph G, int s) {
        paths = new List[G.V()];
        for (int i = 0; i < G.V(); i++) {
            paths[i] = new ArrayList<>();
        }
        pq = new MinPQ<>();
        pq.insert(new DirectedPath(s));
        while (!pq.isEmpty()) {
            DirectedPath p = pq.delMin();
            for (DirectedEdge e : G.adj(p.to())) {
                DirectedPath pp = new DirectedPath(p);
                pp.addEdge(e);
                paths[e.to()].add(pp);
                pq.insert(pp);
            }
        }
    }

    public int numShortestPaths(int v) {
        return paths[v].size();
    }

    public boolean hasKthShortestPath(int v, int k) {
        return numShortestPaths(v) >= k;
    }

    public DirectedPath kthShortestPath(int v, int k) {
        if (!hasKthShortestPath(v, k)) return null;
        return paths[v].get(k - 1);
    }
}
