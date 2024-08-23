package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CrossingEdgeTest {
    @Test
    public void test() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(5);
        Edge e01 = new Edge(0, 1, 0);
        Edge e12 = new Edge(1, 2, 0);
        Edge e23 = new Edge(2, 3, 0);
        Edge e13 = new Edge(1, 3, 0);
        Edge e24 = new Edge(2, 4, 0);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e23);
        G.addEdge(e13);
        G.addEdge(e24);
        assertTrue(new CrossingEdge(G, e01).isCrossingEdge());
        assertFalse(new CrossingEdge(G, e12).isCrossingEdge());
        assertFalse(new CrossingEdge(G, e23).isCrossingEdge());
        assertFalse(new CrossingEdge(G, e13).isCrossingEdge());
        assertTrue(new CrossingEdge(G, e24).isCrossingEdge());
    }
}
