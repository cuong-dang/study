package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class Ex4323Test {
    @Test
    public void test1() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(3);
        Edge e01 = new Edge(0, 1, 0.1);
        Edge e12 = new Edge(1, 2, 1.2);
        Edge e02 = new Edge(0, 2, 0.2);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e02);
        Set<Edge> actual = new HashSet<>();
        new Ex4323(G).mst().forEach(actual::add);
        assertEquals(Set.of(e01, e02), actual);
    }
}
