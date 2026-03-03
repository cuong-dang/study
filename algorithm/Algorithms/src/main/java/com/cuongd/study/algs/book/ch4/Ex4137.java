package com.cuongd.study.algs.book.ch4;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Ex4137 {
    private VertexValueGraph<Pair<Integer, Integer>> G;
    private final int maxPoints;
    private int n;

    public Ex4137(int maxPoints) {
        this.maxPoints = maxPoints;
        G = new VertexValueGraph<>(this.maxPoints);
    }

    public int numPoints() {
        return n;
    }

    public boolean hasPoint(int x1, int y1) {
        return G.vertexWithValue(new ImmutablePair<>(x1, y1)) != null;
    }

    public boolean hasLine(int x1, int y1, int x2, int y2) {
        return hasPoint(x1, y1) &&
                hasPoint(x2, y2) &&
                G.hasEdge(G.vertexWithValue(new ImmutablePair<>(x1, y1)),
                        G.vertexWithValue(new ImmutablePair<>(x2, y2)));
    }

    public void addPoint(int x, int y) {
        if (n == maxPoints) {
            throw new RuntimeException("Graph is full.");
        }
        G.setVertexValue(n++, new ImmutablePair<>(x, y));
    }

    public void addLine(int x1, int y1, int x2, int y2) {
        if (G.vertexWithValue(new ImmutablePair<>(x1, y1)) == null) {
            addPoint(x1, y1);
        }
        if (G.vertexWithValue(new ImmutablePair<>(x2, y2)) == null) {
            addPoint(x2, y2);
        }
        G.addEdge(G.vertexWithValue(new ImmutablePair<>(x1, y1)), G.vertexWithValue(new ImmutablePair<>(x2, y2)));
    }
}
