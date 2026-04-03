package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Ex4426Test {
    @Test
    public void test() {
        AdjEdgeWeightedGraph G = new AdjEdgeWeightedGraph(3);
        G.addEdge(0, 1, .1);
        G.addEdge(1, 2, .2);
        G.addEdge(0, 2, .4);
        Ex4426 actual = new Ex4426(G, 0);
        assertTrue(actual.hasPathTo(1));
        assertTrue(actual.hasPathTo(2));
        assertEquals(List.of(0, 1), actual.pathTo(1));
        assertEquals(List.of(0, 1, 2), actual.pathTo(2));
    }
}
