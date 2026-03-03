package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex4310Test {
    @Test
    public void test() {
        Ex4310 G = new Ex4310(4);
        Edge e1 = new Edge(0, 1, 1);
        Edge e2 = new Edge(0, 2, 2);
        Edge e3 = new Edge(1, 1, 3);
        Edge e4 = new Edge(1, 3, 4);
        assert (G.V() == 4);
        assert (G.E() == 0);
        assertEquals(List.of(), G.adj(0));
        assertEquals(List.of(), G.adj(1));
        assertEquals(List.of(), G.adj(2));
        assertEquals(List.of(), G.adj(3));
        assertEquals(List.of(), G.edges());
        G.addEdge(e1);
        assert (G.E() == 1);
        assertEquals(List.of(e1), G.adj(0));
        assertEquals(List.of(e1), G.adj(1));
        assertEquals(List.of(), G.adj(2));
        assertEquals(List.of(), G.adj(3));
        assertEquals(List.of(e1), G.edges());
        G.addEdge(e2);
        assert (G.E() == 2);
        assertEquals(List.of(e1, e2), G.adj(0));
        assertEquals(List.of(e1), G.adj(1));
        assertEquals(List.of(e2), G.adj(2));
        assertEquals(List.of(), G.adj(3));
        assertEquals(List.of(e1, e2), G.edges());
        G.addEdge(e3);
        assert (G.E() == 3);
        assertEquals(List.of(e1, e2), G.adj(0));
        assertEquals(List.of(e1, e3), G.adj(1));
        assertEquals(List.of(e2), G.adj(2));
        assertEquals(List.of(), G.adj(3));
        assertEquals(List.of(e1, e2, e3), G.edges());
        G.addEdge(e4);
        assert (G.E() == 4);
        assertEquals(List.of(e1, e2), G.adj(0));
        assertEquals(List.of(e1, e3, e4), G.adj(1));
        assertEquals(List.of(e2), G.adj(2));
        assertEquals(List.of(e4), G.adj(3));
        assertEquals(List.of(e1, e2, e3, e4), G.edges());
    }
}
