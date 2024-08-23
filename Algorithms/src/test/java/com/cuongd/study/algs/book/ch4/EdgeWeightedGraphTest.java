package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EdgeWeightedGraphTest {
    @Test
    public void testCycle() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(3);
        Edge e01 = new Edge(0, 1, 0);
        Edge e12 = new Edge(1, 2, 0);
        Edge e02 = new Edge(0, 2, 0);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e02);
        List<Edge> actual = new ArrayList<>();
        G.cycle().forEach(actual::add);
        assertEquals(List.of(e01, e12, e02), actual);
    }

    @Test
    public void testCycle2() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(4);
        Edge e01 = new Edge(0, 1, 0);
        Edge e12 = new Edge(1, 2, 0);
        Edge e23 = new Edge(2, 3, 0);
        Edge e31 = new Edge(3, 1, 0);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e23);
        G.addEdge(e31);
        List<Edge> actual = new ArrayList<>();
        G.cycle().forEach(actual::add);
        assertEquals(List.of(e12, e23, e31), actual);
    }

    @Test
    public void testCycle3() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(5);
        Edge e01 = new Edge(0, 1, 0);
        Edge e12 = new Edge(1, 2, 0);
        Edge e23 = new Edge(2, 3, 0);
        Edge e34 = new Edge(3, 4, 0);
        Edge e41 = new Edge(4, 1, 0);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e23);
        G.addEdge(e34);
        G.addEdge(e41);
        List<Edge> actual = new ArrayList<>();
        G.cycle().forEach(actual::add);
        assertEquals(List.of(e12, e23, e34, e41), actual);
    }

    @Test
    public void testCycle4() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(6);
        Edge e01 = new Edge(0, 1, 0);
        Edge e13 = new Edge(1, 3, 0);
        Edge e02 = new Edge(0, 2, 0);
        Edge e24 = new Edge(2, 4, 0);
        Edge e25 = new Edge(2, 5, 0);
        Edge e45 = new Edge(4, 5, 0);
        G.addEdge(e01);
        G.addEdge(e13);
        G.addEdge(e02);
        G.addEdge(e24);
        G.addEdge(e25);
        G.addEdge(e45);
        List<Edge> actual = new ArrayList<>();
        G.cycle().forEach(actual::add);
        assertEquals(List.of(e24, e45, e25), actual);
    }

    @Test
    public void testRemoveEdge() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(3);
        Edge e01 = new Edge(0, 1, 0);
        Edge e12 = new Edge(1, 2, 0);
        Edge e02 = new Edge(0, 2, 0);
        G.addEdge(e01);
        G.addEdge(e12);
        G.addEdge(e02);
        List<Edge> actual = new ArrayList<>();
        G.removeEdge(e01);
        assertEquals(2, G.E());
        G.edges().forEach(actual::add);
        assertEquals(List.of(e12, e02), actual);
        actual.clear();
        G.removeEdge(e12);
        assertEquals(1, G.E());
        G.edges().forEach(actual::add);
        assertEquals(List.of(e02), actual);

    }
}
