package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SAPTest {
    @Test
    public void test() {
        Digraph G = new Digraph(new In("src/test/resources/wk6/digraph1.txt"));
        SAP sap = new SAP(G);
        assertEquals(4, sap.length(3, 11));
        assertEquals(1, sap.ancestor(3, 11));
        assertEquals(3, sap.length(9, 12));
        assertEquals(5, sap.ancestor(9, 12));
        assertEquals(4, sap.length(7, 2));
        assertEquals(0, sap.ancestor(7, 2));
        assertEquals(-1, sap.length(1, 6));
        assertEquals(-1, sap.ancestor(1, 6));
    }
}
