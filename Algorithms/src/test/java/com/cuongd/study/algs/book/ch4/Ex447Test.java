package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class Ex447Test {
    @Test
    public void test() {
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(3);
        DirectedEdge e01 = new DirectedEdge(0, 1, 0.1);
        assertFalse(new Ex447(G, 0).has2ndShortest(1));

        G.addEdge(e01);
        assertFalse(new Ex447(G, 0).has2ndShortest(1));

        DirectedEdge e02 = new DirectedEdge(0, 2, 0.2);
        G.addEdge(e02);
        assertFalse(new Ex447(G, 0).has2ndShortest(1));
        assertFalse(new Ex447(G, 0).has2ndShortest(2));

        DirectedEdge e21 = new DirectedEdge(2, 1, 0.3);
        G.addEdge(e21);
        assertTrue(new Ex447(G, 0).has2ndShortest(1));
        assertFalse(new Ex447(G, 0).has2ndShortest(2));
        assertEquals(List.of(e02, e21), new Ex447(G, 0).secondShortest(1));
    }
}
