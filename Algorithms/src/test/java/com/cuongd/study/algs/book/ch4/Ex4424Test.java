package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Ex4424Test {
    @Test
    public void test() {
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(4);
        DirectedEdge e02 = new DirectedEdge(0, 2, .1);
        DirectedEdge e12 = new DirectedEdge(1, 2, .2);
        DirectedEdge e13 = new DirectedEdge(1, 3, .1);
        DirectedEdge e23 = new DirectedEdge(2, 3, .3);

        G.addEdge(e02);
        G.addEdge(e12);
        G.addEdge(e13);
        G.addEdge(e23);
        Ex4424 ans = new Ex4424(G, List.of(0, 1));
        assertTrue(ans.hasPathTo(2));
        assertTrue(ans.hasPathTo(3));
        assertEquals(List.of(e02), ans.pathTo(2));
        assertEquals(List.of(e13), ans.pathTo(3));
    }
}
