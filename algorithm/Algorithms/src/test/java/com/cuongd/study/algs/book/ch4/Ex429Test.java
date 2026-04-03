package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex429Test {
    @Test
    public void test() {
        Digraph G = new Digraph(5);
        G.addEdge(0, 1);
        G.addEdge(2, 1);
        G.addEdge(1, 4);
        G.addEdge(3, 4);
        assertTrue(G.isTopologicalOrder(List.of(0, 2, 1, 3, 4)));
        assertTrue(G.isTopologicalOrder2(List.of(0, 2, 1, 3, 4)));
        assertTrue(G.isTopologicalOrder(List.of(2, 0, 3, 1, 4)));
        assertTrue(G.isTopologicalOrder2(List.of(2, 0, 3, 1, 4)));
        assertTrue(G.isTopologicalOrder(List.of(3, 0, 2, 1, 4)));
        assertTrue(G.isTopologicalOrder2(List.of(3, 0, 2, 1, 4)));
        assertTrue(G.isTopologicalOrder(List.of(3, 2, 0, 1, 4)));
        assertTrue(G.isTopologicalOrder2(List.of(3, 2, 0, 1, 4)));

        assertFalse(G.isTopologicalOrder(List.of(1, 0, 2, 3, 4)));
        assertFalse(G.isTopologicalOrder2(List.of(1, 0, 2, 3, 4)));
        assertFalse(G.isTopologicalOrder(List.of(0, 2, 1, 4, 3)));
        assertFalse(G.isTopologicalOrder2(List.of(0, 2, 1, 4, 3)));
        assertFalse(G.isTopologicalOrder(List.of(0, 2, 3, 4, 1)));
        assertFalse(G.isTopologicalOrder2(List.of(0, 2, 3, 4, 1)));
    }
}
