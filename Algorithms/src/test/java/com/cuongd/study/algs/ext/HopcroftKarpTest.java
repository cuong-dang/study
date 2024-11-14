package com.cuongd.study.algs.ext;

import org.junit.Test;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class HopcroftKarpTest {
    @Test
    public void test1() {
        HopcroftKarp.Graph<Integer> g = new HopcroftKarp.Graph<>();
        g.addEdge(0, 5);
        g.addEdge(0, 6);
        g.addEdge(1, 5);
        g.addEdge(2, 6);
        g.addEdge(2, 7);
        g.addEdge(2, 8);
        g.addEdge(3, 8);
        g.addEdge(3, 9);
        g.addEdge(4, 8);
        HopcroftKarp<Integer> hk = new HopcroftKarp<>(g);
        Set<AbstractMap.SimpleEntry<Integer, Integer>> expected = new HashSet<>();
        expected.add(new AbstractMap.SimpleEntry<>(0, 6));
        expected.add(new AbstractMap.SimpleEntry<>(1, 5));
        expected.add(new AbstractMap.SimpleEntry<>(2, 7));
        expected.add(new AbstractMap.SimpleEntry<>(3, 9));
        expected.add(new AbstractMap.SimpleEntry<>(4, 8));
        assertEquals(hk.match(), expected);
    }

    @Test
    public void test2() {
        HopcroftKarp.Graph<Integer> g = new HopcroftKarp.Graph<>();
        g.addEdge(0, 5);
        g.addEdge(0, 6);
        g.addEdge(1, 5);
        g.addEdge(1, 7);
        g.addEdge(1, 8);
        g.addEdge(1, 9);
        g.addEdge(2, 5);
        g.addEdge(2, 7);
        g.addEdge(3, 6);
        g.addEdge(3, 7);
        g.addEdge(3, 8);
        g.addEdge(3, 9);
        g.addEdge(4, 7);
        HopcroftKarp<Integer> hk = new HopcroftKarp<>(g);
        Set<AbstractMap.SimpleEntry<Integer, Integer>> expected = new HashSet<>();
        expected.add(new AbstractMap.SimpleEntry<>(0, 6));
        expected.add(new AbstractMap.SimpleEntry<>(1, 8));
        expected.add(new AbstractMap.SimpleEntry<>(2, 5));
        expected.add(new AbstractMap.SimpleEntry<>(3, 9));
        expected.add(new AbstractMap.SimpleEntry<>(4, 7));
        assertEquals(hk.match(), expected);
    }
}
