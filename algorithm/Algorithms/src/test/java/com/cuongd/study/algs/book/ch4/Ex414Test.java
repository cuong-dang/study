package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex414Test {
    @Test
    public void test() {
        Graph g1 = new Graph(3);
        g1.addEdge(0, 1);
        g1.addEdge(0, 2);
        assertTrue(g1.hasEdge(0, 1));
        assertTrue(g1.hasEdge(1, 0));
        assertTrue(g1.hasEdge(0, 2));
        assertTrue(g1.hasEdge(2, 0));
        assertFalse(g1.hasEdge(1, 2));
        assertFalse(g1.hasEdge(2, 1));
    }
}
