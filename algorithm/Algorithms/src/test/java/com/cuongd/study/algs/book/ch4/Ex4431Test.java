package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex4431Test {
    @Test
    public void test() {
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(4);
        G.addEdge(new DirectedEdge(0, 1, 1));
        G.addEdge(new DirectedEdge(1, 2, -2));
        G.addEdge(new DirectedEdge(2, 3, 3));
        Ex4431 actual = new Ex4431(G);
        assertEquals(1, (int) actual.distTo(0, 1));
        assertEquals(-1, (int) actual.distTo(2, 0));
        assertEquals(2, (int) actual.distTo(0, 3));
        assertEquals(-2, (int) actual.distTo(2, 1));
        assertEquals(1, (int) actual.distTo(1, 3));
    }
}
