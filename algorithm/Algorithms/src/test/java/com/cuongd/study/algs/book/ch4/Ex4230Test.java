package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex4230Test {
    @Test
    public void test() {
        Digraph G = new Digraph(5);
        G.addEdge(0, 1);
        G.addEdge(0, 2);
        G.addEdge(1, 3);
        G.addEdge(2, 3);
        G.addEdge(3, 4);
        G.addEdge(2, 4);
        Ex4230 t = new Ex4230(G);
        assertEquals(List.of(0, 2, 1, 3, 4), t.topologicalSort());
    }

    @Test
    public void test2() {
        Digraph G = new Digraph(5);
        G.addEdge(0, 1);
        G.addEdge(0, 4);
        G.addEdge(2, 0);
        G.addEdge(2, 3);
        G.addEdge(3, 4);
        Ex4230 t = new Ex4230(G);
        assertEquals(List.of(2, 3, 0, 4, 1), t.topologicalSort());
    }
}
