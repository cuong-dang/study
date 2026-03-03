package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex4322Test {
    @Test
    public void test() {
        Edge e01 = new Edge(0, 1, 0.1);
        Edge e02 = new Edge(0, 2, 0.2);
        Edge e12 = new Edge(1, 2, 1.2);
        Edge e34 = new Edge(3, 4, 3.4);
        EdgeWeightedGraph G = new EdgeWeightedGraph(5);
        G.addEdge(e01);
        G.addEdge(e02);
        G.addEdge(e12);
        G.addEdge(e34);
        PrimMSF msf1 = new PrimMSF(G);
        KruskalMSF msf2 = new KruskalMSF(G);

        assertEquals(2, msf1.msf().size());
        assertEquals(2, msf2.msf().size());

        List<Edge> actual = new ArrayList<>();
        msf1.msf().get(0).forEach(actual::add);
        assertEquals(List.of(e02, e01), actual);
        actual.clear();

        msf1.msf().get(1).forEach(actual::add);
        assertEquals(List.of(e34), actual);
        actual.clear();

        msf2.msf().get(0).forEach(actual::add);
        assertEquals(List.of(e02, e01), actual);
        actual.clear();

        msf2.msf().get(1).forEach(actual::add);
        assertEquals(List.of(e34), actual);
        actual.clear();
    }
}
