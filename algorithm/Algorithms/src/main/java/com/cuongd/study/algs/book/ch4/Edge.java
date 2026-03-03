package com.cuongd.study.algs.book.ch4;

public class Edge implements Comparable<Edge> {
    private final int v;
    private final int w;
    private final double weight;

    public Edge(int v, int w, double weight) {
        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    public double weight() {
        return weight;
    }

    public int either() {
        return v;
    }

    public int other(int vertex) {
        if (vertex == v) return w;
        if (vertex == w) return v;
        else throw new RuntimeException("Inconsistent edge");
    }

    public int compareTo(Edge that) {
        return Double.compare(this.weight(), that.weight());
    }

    @Override
    public String toString() {
        return String.format("%d-%d %.2f", v, w, weight);
    }
}
