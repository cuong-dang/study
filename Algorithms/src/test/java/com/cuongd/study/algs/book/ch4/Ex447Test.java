package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import org.junit.Test;

import static org.junit.Assert.*;

public class Ex447Test {
    @Test
    public void test() {
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(3);
        DirectedEdge e01 = new DirectedEdge(0, 1, 0.1);
        assertFalse(new Ex447(G, 0).hasKthShortestPath(1, 2));

        G.addEdge(e01);
        assertTrue(new Ex447(G, 0).hasKthShortestPath(1, 1));
        assertFalse(new Ex447(G, 0).hasKthShortestPath(1, 2));

        DirectedEdge e02 = new DirectedEdge(0, 2, 0.2);
        G.addEdge(e02);
        assertTrue(new Ex447(G, 0).hasKthShortestPath(1, 1));
        assertFalse(new Ex447(G, 0).hasKthShortestPath(1, 2));
        assertTrue(new Ex447(G, 0).hasKthShortestPath(2, 1));
        assertFalse(new Ex447(G, 0).hasKthShortestPath(2, 2));

        DirectedEdge e21 = new DirectedEdge(2, 1, 0.3);
        G.addEdge(e21);
        Ex447 ksp = new Ex447(G, 0);
        assertTrue(ksp.hasKthShortestPath(1, 1));
        assertTrue(ksp.hasKthShortestPath(1, 2));
        assertEquals(new DirectedPath(e01), ksp.kthShortestPath(1, 1));
        assertEquals(new DirectedPath(e02, e21), ksp.kthShortestPath(1, 2));
    }

    @Test
    public void test2() {
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(4);
        DirectedEdge e01 = new DirectedEdge(0, 1, 0.1);
        DirectedEdge e02 = new DirectedEdge(0, 2, 0.3);
        DirectedEdge e13 = new DirectedEdge(1, 3, 0.2);
        DirectedEdge e21 = new DirectedEdge(2, 1, 0.4);
        DirectedEdge e23 = new DirectedEdge(2, 3, 0.1);
        G.addEdge(e01);
        G.addEdge(e02);
        G.addEdge(e13);
        G.addEdge(e21);
        G.addEdge(e23);
        Ex447 ksp = new Ex447(G, 0);
        assertEquals(2, ksp.numShortestPaths(1));
        assertEquals(new DirectedPath(e01), ksp.kthShortestPath(1, 1));
        assertEquals(new DirectedPath(e02, e21), ksp.kthShortestPath(1, 2));

        assertEquals(3, ksp.numShortestPaths(3));
        assertEquals(new DirectedPath(e01, e13), ksp.kthShortestPath(3, 1));
        assertEquals(new DirectedPath(e02, e23), ksp.kthShortestPath(3, 2));
        assertEquals(new DirectedPath(e02, e21, e13), ksp.kthShortestPath(3, 3));
    }
}
