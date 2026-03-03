package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.In;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex4118Test {
    @Test
    public void test() {
        Graph G = new Graph(new In("ch4/ex4116.txt"));
        G.addEdge(5, 6);
        GraphProperties gp = new GraphProperties(G);
        assertEquals(3, gp.girth());
    }

    @Test
    public void test2() {
        Graph G = new Graph(8);
        G.addEdge(0, 1);
        G.addEdge(0, 2);
        G.addEdge(0, 3);
        GraphProperties gp = new GraphProperties(G);
        assertEquals(Integer.MAX_VALUE, gp.girth());
        G.addEdge(1, 4);
        G.addEdge(3, 6);
        G.addEdge(4, 5);
        G.addEdge(5, 6);
        gp = new GraphProperties(G);
        assertEquals(6, gp.girth());
    }
}
