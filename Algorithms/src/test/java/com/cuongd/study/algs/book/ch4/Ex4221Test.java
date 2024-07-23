package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex4221Test {
    @Test
    public void testLength1() {
        Digraph G = new Digraph(3);
        DirectedDFS dfs = new DirectedDFS(G, 0);
        assertEquals(0, dfs.length(0));
        assertEquals(-1, dfs.length(1));
        assertEquals(-1, dfs.length(2));

        G.addEdge(0, 1);
        G.addEdge(1, 2);
        dfs = new DirectedDFS(G, 0);
        assertEquals(0, dfs.length(0));
        assertEquals(1, dfs.length(1));
        assertEquals(2, dfs.length(2));

        dfs = new DirectedDFS(G, 1);
        assertEquals(-1, dfs.length(0));
        assertEquals(0, dfs.length(1));
        assertEquals(1, dfs.length(2));
    }

    @Test
    public void test() {
        Digraph G = new Digraph(3);
        LCA lca = new LCA(G);
        assertEquals(-1, lca.lca(0, 1));
        G.addEdge(0, 1);
        assertEquals(0, lca.lca(0, 1));
        G.addEdge(1, 2);
        assertEquals(0, lca.lca(0, 1));
        assertEquals(0, lca.lca(0, 2));
        assertEquals(1, lca.lca(1, 2));
    }

    @Test
    public void test2() {
        Digraph G = new Digraph(3);
        LCA lca = new LCA(G);
        G.addEdge(0, 1);
        G.addEdge(0, 2);
        assertEquals(0, lca.lca(0, 1));
        assertEquals(0, lca.lca(0, 2));
        assertEquals(0, lca.lca(1, 2));
    }

    @Test
    public void test3() {
        Digraph G = new Digraph(9);
        LCA lca = new LCA(G);
        G.addEdge(0, 1);
        G.addEdge(0, 2);
        G.addEdge(1, 3);
        G.addEdge(1, 4);
        G.addEdge(2, 5);
        G.addEdge(2, 6);
        G.addEdge(4, 7);
        G.addEdge(4, 8);

        assertEquals(0, lca.lca(0, 1));
        assertEquals(0, lca.lca(1, 2));
        assertEquals(1, lca.lca(3, 4));
        assertEquals(1, lca.lca(3, 7));
        assertEquals(0, lca.lca(3, 5));
        assertEquals(0, lca.lca(5, 7));
    }

    @Test
    public void test4() {
        Digraph G = new Digraph(6);
        LCA lca = new LCA(G);
        G.addEdge(0, 1);
        G.addEdge(1, 2);
        G.addEdge(1, 3);
        G.addEdge(3, 4);
        G.addEdge(4, 5);
        G.addEdge(0, 5);

        assertEquals(1, lca.lca(2, 5));
    }
}
