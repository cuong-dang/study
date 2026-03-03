package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class Ex4332Test {
    @Test
    public void test1() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(3);
        Edge e01 = new Edge(0, 1, 0.1);
        Edge e02 = new Edge(0, 2, 0.2);
        Edge e12 = new Edge(1, 2, 1.2);
        G.addEdge(e01);
        G.addEdge(e02);
        G.addEdge(e12);
        assertEquals(List.of(e02, e01), new Ex4332(G, Set.of()).mst());
        assertEquals(List.of(e02, e01), new Ex4332(G, Set.of(e01)).mst());
        assertEquals(List.of(e02, e01), new Ex4332(G, Set.of(e01, e02)).mst());
        assertEquals(List.of(e12, e01), new Ex4332(G, Set.of(e12)).mst());
        assertEquals(List.of(e12, e02), new Ex4332(G, Set.of(e12, e02)).mst());
    }

    @Test
    public void test2() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(4);
        Edge e01 = new Edge(0, 1, 0.1);
        Edge e03 = new Edge(0, 3, 0.3);
        Edge e12 = new Edge(1, 2, 1.2);
        Edge e13 = new Edge(1, 3, 1.3);
        Edge e23 = new Edge(2, 3, 2.3);
        G.addEdge(e01);
        G.addEdge(e03);
        G.addEdge(e12);
        G.addEdge(e13);
        G.addEdge(e23);
        assertEquals(List.of(e12, e03, e01), new Ex4332(G, Set.of()).mst());
        assertEquals(List.of(e23, e03, e01), new Ex4332(G, Set.of(e23)).mst());
        assertEquals(List.of(e23, e13, e01), new Ex4332(G, Set.of(e23, e13)).mst());
        assertEquals(List.of(e23, e13, e03), new Ex4332(G, Set.of(e23, e13, e03)).mst());
        assertEquals(List.of(e12, e13, e01), new Ex4332(G, Set.of(e12, e13)).mst());
    }
}
