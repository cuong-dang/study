package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.In;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex4116Test {
    @Test
    public void test() {
        Graph G = new Graph(new In("ch4/ex4116.txt"));
        GraphProperties gp = new GraphProperties(G);
        assertEquals(3, gp.eccentricity(0));
        assertEquals(3, gp.eccentricity(1));
        assertEquals(4, gp.eccentricity(2));
        assertEquals(4, gp.eccentricity(3));
        assertEquals(4, gp.eccentricity(4));
        assertEquals(4, gp.eccentricity(5));
        assertEquals(5, gp.eccentricity(6));
        assertEquals(5, gp.eccentricity(7));

        assertEquals(5, gp.diameter());
        assertEquals(3, gp.radius());
        assertEquals(0, gp.center());
    }
}
