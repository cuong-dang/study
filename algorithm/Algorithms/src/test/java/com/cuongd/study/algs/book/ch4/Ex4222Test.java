package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex4222Test {
    @Test
    public void test1() {
        Digraph G = new Digraph(3);
        G.addEdge(0, 1);
        G.addEdge(0, 2);
        assertEquals(List.of(1, 0, 2), new LCA(G).sap(1, 2));
    }

    @Test
    public void test2() {
        Digraph G = new Digraph(4);
        G.addEdge(0, 1);
        G.addEdge(0, 2);
        G.addEdge(2, 3);
        assertEquals(List.of(1, 0, 2, 3), new LCA(G).sap(1, 3));
    }

    @Test
    public void test3() {
        Digraph G = new Digraph(6);
        G.addEdge(0, 1);
        G.addEdge(1, 2);
        G.addEdge(1, 3);
        G.addEdge(3, 4);
        G.addEdge(4, 5);
        G.addEdge(0, 5);
        assertEquals(List.of(2, 1, 0, 5), new LCA(G).sap(2, 5));
    }
}
