package com.cuongd.study.algs.book.ch2;

public class Ex2429<Key extends Comparable<Key>> {
    private final MinPQ<Key> minPq;
    private final MaxPQ<Key> maxPq;
    private int N;

    public Ex2429() {
        minPq = new MinPQ<>();
        maxPq = new MaxPQ<>();
    }

    public void insert(Key item) {
        minPq.insert(item);
        maxPq.insert(item);
        ++N;
    }

    public Key delMax() {
        if (N == 0)
            throw new IllegalStateException();
        --N;
        return maxPq.delMax();
    }

    public Key delMin() {
        if (N == 0)
            throw new IllegalStateException();
        --N;
        return minPq.delMax();
    }

    public Key max() {
        return maxPq.peek();
    }

    public Key min() {
        return minPq.peek();
    }
}
