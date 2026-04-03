package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex4136Test {
    @Test
    public void test() {
        Graph G = new Graph(1);
        assertTrue(new Ex4136(G).isEdgeConnected());

        G = new Graph(2);
        G.addEdge(0, 1);
        assertFalse(new Ex4136(G).isEdgeConnected());

        G = new Graph(3);
        G.addEdge(0, 1);
        G.addEdge(1, 2);
        assertFalse(new Ex4136(G).isEdgeConnected());
        G.addEdge(0, 2);
        assertTrue(new Ex4136(G).isEdgeConnected());

        G = new Graph(4);
        G.addEdge(0, 1);
        G.addEdge(1, 2);
        G.addEdge(2, 3);
        assertFalse(new Ex4136(G).isEdgeConnected());

        G = new Graph(4);
        G.addEdge(0, 1);
        G.addEdge(0, 2);
        G.addEdge(0, 3);
        assertFalse(new Ex4136(G).isEdgeConnected());

        G = new Graph(4);
        G.addEdge(0, 1);
        G.addEdge(1, 2);
        G.addEdge(2, 3);
        G.addEdge(3, 0);
        assertTrue(new Ex4136(G).isEdgeConnected());

        G = new Graph(4);
        G.addEdge(0, 1);
        G.addEdge(0, 2);
        G.addEdge(0, 3);
        G.addEdge(1, 2);
        assertFalse(new Ex4136(G).isEdgeConnected());

        G = new Graph(4);
        G.addEdge(0, 1);
        G.addEdge(0, 2);
        G.addEdge(0, 3);
        G.addEdge(1, 2);
        G.addEdge(1, 3);
        G.addEdge(2, 3);
        assertTrue(new Ex4136(G).isEdgeConnected());
    }
}
