package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.In;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex4113Test {
    @Test
    public void test() {
        Graph g = new Graph(new In("ch4/tinyGex2.txt"));
        BreadthFirstPaths bfp = new BreadthFirstPaths(g, 0);
        assertEquals(0, bfp.distTo(0));
        assertEquals(1, bfp.distTo(6));
        assertEquals(1, bfp.distTo(2));
        assertEquals(2, bfp.distTo(3));
        assertEquals(2, bfp.distTo(5));
        assertEquals(3, bfp.distTo(10));
    }
}
