package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex4436Test {
    @Test
    public void test() {
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(5);
        G.addEdge(new DirectedEdge(0, 1, 0.1));
        G.addEdge(new DirectedEdge(0, 2, 0.2));
        G.addEdge(new DirectedEdge(1, 3, 0.3));
        G.addEdge(new DirectedEdge(2, 4, 0.4));
        Ex4436 actual = new Ex4436(G);
        assertEquals(List.of(), actual.nearest(0, 0.05));
        assertEquals(List.of(1), actual.nearest(0, 0.1));
        assertEquals(List.of(1), actual.nearest(0, 0.15));
        assertEquals(List.of(1, 2), actual.nearest(0, 0.2));
        assertEquals(List.of(1, 2, 3), actual.nearest(0, 0.45));
        assertEquals(List.of(1, 2, 3, 4), actual.nearest(0, 0.65));
    }
}
