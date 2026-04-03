package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex4333Test {
    @Test
    public void testIsSpanningTree() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(3);
        Edge e01 = new Edge(0, 1, 0.1);
        Edge e02 = new Edge(0, 2, 0.2);
        Edge e12 = new Edge(1, 2, 1.2);
        G.addEdge(e01);
        G.addEdge(e02);
        G.addEdge(e12);
        assertFalse(new Ex4333(G, Set.of()).isSpanningTree());
        assertFalse(new Ex4333(G, Set.of(e01)).isSpanningTree());
        assertFalse(new Ex4333(G, Set.of(e02)).isSpanningTree());
        assertFalse(new Ex4333(G, Set.of(e12)).isSpanningTree());
        assertFalse(new Ex4333(G, Set.of(e01, e02, e12)).isSpanningTree());
        assertTrue(new Ex4333(G, Set.of(e01, e02)).isSpanningTree());
        assertTrue(new Ex4333(G, Set.of(e01, e12)).isSpanningTree());
        assertTrue(new Ex4333(G, Set.of(e02, e12)).isSpanningTree());
    }

    @Test
    public void testIsMst1() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(3);
        Edge e01 = new Edge(0, 1, 0.1);
        Edge e02 = new Edge(0, 2, 0.2);
        Edge e12 = new Edge(1, 2, 1.2);
        G.addEdge(e01);
        G.addEdge(e02);
        G.addEdge(e12);
        assertTrue(new Ex4333(G, Set.of(e01, e02)).isMst());
        assertFalse(new Ex4333(G, Set.of(e01, e12)).isMst());
        assertFalse(new Ex4333(G, Set.of(e02, e12)).isMst());
    }

    @Test
    public void testIsMst2() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(5);
        Edge e01 = new Edge(0, 1, 0.1);
        Edge e12 = new Edge(1, 2, 1.2);
        Edge e23 = new Edge(2, 3, 2.3);
        Edge e24 = new Edge(2, 4, 2.4);
        Edge e34 = new Edge(3, 4, 3.4);
        Edge e13 = new Edge(1, 3, 2.5);
        Edge e04 = new Edge(0, 4, 2.6);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e23);
        G.addEdge(e24);
        G.addEdge(e34);
        G.addEdge(e13);
        G.addEdge(e04);
        Set<Edge> mst = new HashSet<>();
        assertTrue(new Ex4333(G, Set.of(e01, e12, e23, e24)).isMst());
        assertFalse(new Ex4333(G, Set.of(e01, e13, e23, e24)).isMst());
        assertFalse(new Ex4333(G, Set.of(e04, e12, e13, e24)).isMst());
        assertFalse(new Ex4333(G, Set.of(e04, e12, e23, e24)).isMst());
        assertFalse(new Ex4333(G, Set.of(e01, e12, e23, e04)).isMst());
    }
}
