package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex4316Test {
    @Test
    public void test() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(3);
        G.addEdge(new Edge(0, 1, 0.1));
        G.addEdge(new Edge(1, 2, 0.2));
        assertEquals(0.2, new Ex4316(G, new Edge(0, 2, 0)).maxW(), 0.0000001);
    }
}
