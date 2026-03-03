package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.BellmanFordSP;
import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

import java.util.ArrayList;
import java.util.List;

public class Ex4430 {
    private EdgeWeightedDigraph G;

    public Ex4430(EdgeWeightedDigraph G) {
        this.G = G;
    }

    public List<DirectedEdge> pathTo(int from, int to) {
        BellmanFordSP bf = new BellmanFordSP(G, from);
        EdgeWeightedDigraph GPrime = new EdgeWeightedDigraph(G.V());
        for (DirectedEdge e : G.edges()) {
            GPrime.addEdge(new DirectedEdge(e.from(), e.to(),
                    e.weight() + bf.distTo(e.from()) - bf.distTo(e.to())));
        }
        DijkstraSP sp = new DijkstraSP(GPrime, from);
        List<DirectedEdge> result = new ArrayList<>();
        sp.pathTo(to).forEach(result::add);
        return result;
    }
}
