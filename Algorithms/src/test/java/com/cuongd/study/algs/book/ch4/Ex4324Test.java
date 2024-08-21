package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex4324Test {
    @Test
    public void test() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(5);
        Edge e01 = new Edge(0, 1, 0.1);
        Edge e12 = new Edge(1, 2, 0.12);
        Edge e23 = new Edge(2, 3, 0.23);
        Edge e13 = new Edge(1, 3, 0.13);
        Edge e24 = new Edge(2, 4, 0.24);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e23);
        G.addEdge(e13);
        G.addEdge(e24);
        List<Edge> actual = new ArrayList<>();
        new Ex4324(G).mst().forEach(actual::add);
        assertEquals(List.of(e01, e12, e13, e24), actual);
    }
}
