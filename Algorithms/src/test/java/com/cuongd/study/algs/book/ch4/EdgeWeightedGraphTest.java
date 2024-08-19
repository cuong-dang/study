package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EdgeWeightedGraphTest {
    @Test
    public void testCycle() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(3);
        G.addEdge(new Edge(0, 1, 0));
        G.addEdge(new Edge(1, 2, 0));
        G.addEdge(new Edge(0, 2, 0));
        List<Integer> actual = new ArrayList<>();
        G.cycle().forEach(actual::add);
        assertEquals(List.of(0, 2, 1), actual);
    }

    @Test
    public void testCycle2() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(4);
        G.addEdge(new Edge(0, 1, 0));
        G.addEdge(new Edge(1, 2, 0));
        G.addEdge(new Edge(2, 3, 0));
        G.addEdge(new Edge(3, 1, 0));
        List<Integer> actual = new ArrayList<>();
        G.cycle().forEach(actual::add);
        assertEquals(List.of(1, 3, 2), actual);
    }

    @Test
    public void testCycle3() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(5);
        G.addEdge(new Edge(0, 1, 0));
        G.addEdge(new Edge(1, 2, 0));
        G.addEdge(new Edge(2, 3, 0));
        G.addEdge(new Edge(3, 4, 0));
        G.addEdge(new Edge(4, 1, 0));
        List<Integer> actual = new ArrayList<>();
        G.cycle().forEach(actual::add);
        assertEquals(List.of(1, 4, 3, 2), actual);
    }

    @Test
    public void testCycle4() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(6);
        G.addEdge(new Edge(0, 1, 0));
        G.addEdge(new Edge(1, 3, 0));
        G.addEdge(new Edge(0, 2, 0));
        G.addEdge(new Edge(2, 4, 0));
        G.addEdge(new Edge(2, 5, 0));
        G.addEdge(new Edge(4, 5, 0));
        List<Integer> actual = new ArrayList<>();
        G.cycle().forEach(actual::add);
        assertEquals(List.of(2, 5, 4), actual);
    }
}
