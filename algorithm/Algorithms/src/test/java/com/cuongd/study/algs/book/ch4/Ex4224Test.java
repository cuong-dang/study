package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex4224Test {
    @Test
    public void test() {
        Digraph G = new Digraph(5);
        G.addEdge(0, 1);
        assertFalse(new Ex4224(G).hasHamiltonianPath());
        G.addEdge(0, 2);
        assertFalse(new Ex4224(G).hasHamiltonianPath());
        G.addEdge(1, 3);
        assertFalse(new Ex4224(G).hasHamiltonianPath());
        G.addEdge(3, 4);
        assertFalse(new Ex4224(G).hasHamiltonianPath());
        G.addEdge(4, 2);
        assertTrue(new Ex4224(G).hasHamiltonianPath());
    }
}
