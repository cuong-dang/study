package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Ex4430Test {
    @Test
    public void test() {
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(4);
        DirectedEdge e01 = new DirectedEdge(0, 1, .1);
        DirectedEdge e02 = new DirectedEdge(0, 2, .4);
        DirectedEdge e12 = new DirectedEdge(1, 2, .2);
        DirectedEdge e13 = new DirectedEdge(1, 3, .7);
        DirectedEdge e23 = new DirectedEdge(2, 3, .2);
        Ex4430 actual = new Ex4430(G);
        G.addEdge(e01);
        G.addEdge(e02);
        G.addEdge(e12);
        G.addEdge(e13);
        G.addEdge(e23);
        assertEquals(List.of(List.of(0, 1)),
                actual.pathTo(0, 1)
                        .stream().map(e -> List.of(e.from(), e.to()))
                        .collect(Collectors.toList()));
        assertEquals(List.of(List.of(0, 1), List.of(1, 2)),
                actual.pathTo(0, 2)
                        .stream().map(e -> List.of(e.from(), e.to()))
                        .collect(Collectors.toList()));
        assertEquals(List.of(List.of(1, 2), List.of(2, 3)),
                actual.pathTo(1, 3)
                        .stream().map(e -> List.of(e.from(), e.to()))
                        .collect(Collectors.toList()));
    }
}
