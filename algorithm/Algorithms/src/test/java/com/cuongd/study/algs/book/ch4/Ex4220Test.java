package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex4220Test {
    @Test
    public void test1() {
        Digraph G = new Digraph(5);
        G.addEdge(0, 1);
        G.addEdge(0, 3);
        G.addEdge(1, 2);
        G.addEdge(2, 0);
        G.addEdge(3, 4);
        G.addEdge(4, 0);
        assertTrue(new Euler(G).hasEulerianCycle());
    }

    @Test
    public void test2() {
        Digraph G = new Digraph(5);
        G.addEdge(0, 1);
        G.addEdge(1, 2);
        G.addEdge(2, 3);
        G.addEdge(3, 4);
        assertFalse(new Euler(G).hasEulerianCycle());
    }
}
