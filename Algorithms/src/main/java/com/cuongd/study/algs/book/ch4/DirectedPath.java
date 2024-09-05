package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.DirectedEdge;

import java.util.ArrayList;
import java.util.List;

public class DirectedPath implements Comparable<DirectedPath> {
    private List<DirectedEdge> path;
    private int to;
    private int length;
    private double weight;

    public DirectedPath(int s) {
        path = new ArrayList<>();
        to = s;
    }

    public DirectedPath(DirectedPath p) {
        path = new ArrayList<>(p.path);
        to = p.to;
        length = p.length;
        weight = p.weight;
    }

    public DirectedPath(DirectedEdge... es) {
        path = new ArrayList<>();
        for (DirectedEdge e : es) {
            addEdge(e);
        }
    }

    public int to() {
        return to;
    }

    public int length() {
        return length;
    }

    public double weight() {
        return weight;
    }

    public void addEdge(DirectedEdge e) {
        path.add(e);
        length++;
        weight += e.weight();
        to = e.to();
    }

    @Override
    public int compareTo(DirectedPath o) {
        return Double.compare(weight, o.weight);
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectedPath that = (DirectedPath) o;
        if (length != that.length) return false;
        for (int i = 0; i < length; i++) {
            DirectedEdge e = path.get(i), ee = that.path.get(i);
            if (e.from() != ee.from() || e.to() != ee.to() || e.weight() != ee.weight()) {
                return false;
            }
        }
        return true;
    }
}
