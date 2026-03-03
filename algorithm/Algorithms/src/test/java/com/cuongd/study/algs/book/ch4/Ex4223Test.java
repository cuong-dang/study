package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class Ex4223Test {
    @Test
    public void testStrongCC() {
        Digraph G = new Digraph(4);
        G.addEdge(0, 1);
        G.addEdge(1, 2);
        G.addEdge(2, 3);
        G.addEdge(3, 1);
        Ex4223 scc = new Ex4223(G);
        assertEquals(Set.of(0), scc.strongCC(0));
        assertEquals(Set.of(1, 2, 3), scc.strongCC(1));

        assertEquals(0, scc.id(0));
        assertEquals(1, scc.id(1));
        assertEquals(1, scc.id(2));
        assertEquals(1, scc.id(3));
    }
}
