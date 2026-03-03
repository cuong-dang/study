package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex4129Test {
    @Test
    public void parallelEdges() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        assertFalse(new Cycle(g).hasCycle());
        g.addEdge(0, 2);
        assertTrue(new Cycle(g).hasCycle());
    }

    @Test
    public void selfLoops() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(0, 0);
        g.addEdge(2, 2);
        assertFalse(new Cycle(g).hasCycle());
        g.addEdge(0, 2);
        assertTrue(new Cycle(g).hasCycle());
    }
}
