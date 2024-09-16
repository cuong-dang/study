package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ex4434Test {
    @Test
    public void test() {
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(4);
        DirectedEdge e01 = new DirectedEdge(0, 1, 0.1);
        DirectedEdge e12 = new DirectedEdge(1, 2, 0.3);
        DirectedEdge e23 = new DirectedEdge(2, 3, 0.2);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e23);
        Ex4434 actual = new Ex4434(G, 0);
        assertEquals(List.of(e01), actual.pathTo(1));
        assertEquals(List.of(e01, e12), actual.pathTo(2));
        assertNull(actual.pathTo(3));
    }

    @Test
    public void test2() {
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(5);
        DirectedEdge e01 = new DirectedEdge(0, 1, 0.1);
        DirectedEdge e12 = new DirectedEdge(1, 2, 0.3);
        DirectedEdge e23 = new DirectedEdge(2, 3, 0.2);
        DirectedEdge e04 = new DirectedEdge(0, 4, 0.12);
        DirectedEdge e42 = new DirectedEdge(4, 2, 0.11);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e23);
        G.addEdge(e04);
        G.addEdge(e42);
        Ex4434 actual = new Ex4434(G, 0);
        assertEquals(List.of(e01), actual.pathTo(1));
        assertEquals(List.of(e04, e42), actual.pathTo(2));
        assertNull(actual.pathTo(3));
    }
}
