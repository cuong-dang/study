package com.cuongd.study.algs.book.ch4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class Ex415Test {
    @Rule
    public ExpectedException ee = ExpectedException.none();

    @Test
    public void test1() {
        Graph g = new Graph(3);
        g.addEdge(0, 1);
        ee.expect(IllegalArgumentException.class);
        ee.expectMessage("Self-loops are disallowed");
        g.addEdge(0, 0);
    }

    @Test
    public void test2() {
        Graph g = new Graph(3);
        g.addEdge(0, 1);
        ee.expect(IllegalArgumentException.class);
        ee.expectMessage("Parallel edges are disallowed");
        g.addEdge(0, 1);
    }
}
