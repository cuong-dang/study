package com.cuongd.study.algs.book.ch4;

import java.util.HashMap;
import java.util.Map;

public class VertexValueGraph<T> extends Graph {
    private final Map<Integer, T> vertexValues;
    private final Map<T, Integer> valueVertices;

    public VertexValueGraph(int V) {
        super(V);
        vertexValues = new HashMap<>();
        valueVertices = new HashMap<>();
    }

    public void setVertexValue(int v, T val) {
        vertexValues.put(v, val);
        valueVertices.put(val, v);
    }

    public T getVertexValue(int v) {
        return vertexValues.get(v);
    }

    public Integer vertexWithValue(T val) {
        return valueVertices.get(val);
    }

    public Iterable<T> vertexValues() {
        return vertexValues.values();
    }
}
