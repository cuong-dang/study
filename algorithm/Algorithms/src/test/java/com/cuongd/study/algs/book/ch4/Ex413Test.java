package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex413Test {
    @Test
    public void test() {
        Graph g1 = new Graph(3);
        g1.addEdge(0, 1);
        g1.addEdge(0, 2);
        Graph g2 = new Graph(g1);
        assertG2(g2);

        g1.addEdge(1, 2);
        assertG2(g2);
    }

    private void assertG2(Graph g2) {
        assertEquals(3, g2.V());
        assertEquals(2, g2.E());
        List<Integer> actual = new ArrayList<>();
        g2.adj(0).forEach(actual::add);
        assertEquals(List.of(1, 2), actual);
        actual.clear();
        g2.adj(1).forEach(actual::add);
        assertEquals(List.of(0), actual);
        actual.clear();
        g2.adj(2).forEach(actual::add);
        assertEquals(List.of(0), actual);
    }
}
