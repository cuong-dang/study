package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SAPTest {
    @Test
    public void test() {
        Digraph G = new Digraph(new In("src/test/resources/wk6/digraph1.txt"));
        SAP sap = new SAP(G);
//        assertEquals(-1, sap.length(new ArrayList<>(), new ArrayList<>()));
//        assertEquals(-1, sap.ancestor(new ArrayList<>(), new ArrayList<>()));
        assertEquals(4, sap.length(3, 11));
        assertEquals(1, sap.ancestor(3, 11));
        assertEquals(3, sap.length(9, 12));
        assertEquals(5, sap.ancestor(9, 12));
        assertEquals(4, sap.length(7, 2));
        assertEquals(0, sap.ancestor(7, 2));
        assertEquals(-1, sap.length(1, 6));
        assertEquals(-1, sap.ancestor(1, 6));
        assertEquals(1, sap.length(11, 10));
        assertEquals(0, sap.length(0, 0));
        assertEquals(3, sap.length(1, 11));
        assertEquals(1, sap.ancestor(1, 11));
    }

    @Test
    public void test2() {
        Digraph G = new Digraph(new In("src/test/resources/wk6/digraph25.txt"));
        SAP sap = new SAP(G);
        assertEquals(4, sap.length(List.of(13, 23, 24), List.of(6, 16, 17)));
        assertEquals(3, sap.ancestor(List.of(13, 23, 24), List.of(6, 16, 17)));
    }

    @Test
    public void test3() {
        Digraph G = new Digraph(new In("src/test/resources/wk6/my_digraph1.txt"));
        SAP sap = new SAP(G);
        assertEquals(2, sap.length(2, 0));
        assertEquals(2, sap.ancestor(2, 0));
        assertEquals(1, sap.length(1, 3));
        assertEquals(1, sap.ancestor(1, 3));
        assertEquals(1, sap.length(1, 2));
        assertEquals(2, sap.ancestor(1, 2));
        assertEquals(2, sap.length(0, 3));
        assertEquals(1, sap.ancestor(0, 3));
    }

    @Test
    public void test4() {
        Digraph G = new Digraph(new In("src/test/resources/wk6/my_digraph2.txt"));
        SAP sap = new SAP(G);
        assertEquals(1, sap.length(0, 1));
        assertEquals(1, sap.ancestor(0, 1));
        assertEquals(-1, sap.length(0, 2));
        assertEquals(-1, sap.ancestor(0, 2));
        assertEquals(-1, sap.length(1, 2));
        assertEquals(-1, sap.ancestor(1, 2));
    }

    @Test
    public void test5() {
        Digraph G = new Digraph(new In("src/test/resources/wk6/my_digraph3.txt"));
        SAP sap = new SAP(G);
        assertEquals(3, sap.length(0, 3));
        assertEquals(4, sap.ancestor(0, 3));
        assertEquals(3, sap.length(5, 7));
        assertEquals(4, sap.ancestor(5, 7));
    }

    @Test
    public void test6() {
        Digraph G = new Digraph(new In("src/test/resources/wk6/my_digraph4.txt"));
        SAP sap = new SAP(G);
        assertEquals(2, sap.length(4, 5));
        assertEquals(2, sap.ancestor(4, 5));
        assertEquals(3, sap.length(1, 5));
        assertEquals(0, sap.ancestor(1, 5));
    }

    @Test
    public void test7() {
        Digraph G = new Digraph(new In("src/test/resources/wk6/digraph4.txt"));
        SAP sap = new SAP(G);
        assertEquals(3, sap.length(4, 1));
    }
}
