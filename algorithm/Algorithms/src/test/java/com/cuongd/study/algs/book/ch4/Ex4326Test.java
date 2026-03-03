package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex4326Test {
    @Test
    public void test() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(4);
        Edge e01 = new Edge(0, 1, 0.1);
        Edge e12 = new Edge(1, 2, 0.2);
        Edge e13 = new Edge(1, 3, 0.3);
        Edge e23 = new Edge(2, 3, 0.3);
        Edge e03 = new Edge(0, 3, 0.4);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e13);
        G.addEdge(e23);
        G.addEdge(e03);
        PrimMST mst = new PrimMST(G);
        assertTrue(mst.isCritical(e01));
        assertTrue(mst.isCritical(e12));
        assertFalse(mst.isCritical(e13));
        assertFalse(mst.isCritical(e23));
        assertFalse(mst.isCritical(e03));
    }
}
