package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.In;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Ex4115Test {
    @Test
    public void test() {
        Graph g = new Graph(new In("ch4/tinyGadj.txt"));
        assertEquals(13, g.V());
        assertEquals(13, g.E());
        assertTrue(g.hasEdge(0, 1));
        assertTrue(g.hasEdge(0, 2));
        assertTrue(g.hasEdge(0, 5));
        assertTrue(g.hasEdge(0, 6));
        assertTrue(g.hasEdge(1, 0));
        assertTrue(g.hasEdge(2, 0));
        assertTrue(g.hasEdge(3, 4));
        assertTrue(g.hasEdge(3, 5));
        assertTrue(g.hasEdge(4, 3));
        assertTrue(g.hasEdge(4, 5));
        assertTrue(g.hasEdge(4, 6));
    }
}
