package com.cuongd.study.algs.book.ch2;

public class Ex2433<Key extends Comparable<Key>> {
    private final int[] pq;
    private final int[] qp;
    private final Key[] keys;
    private int N;

    public Ex2433(int maxN) {
        //noinspection unchecked
        keys = (Key[]) new Comparable[maxN];
        pq = new int[maxN + 1];
        qp = new int[maxN];

        for (int i = 0; i < maxN; ++i)
            qp[i] = -1;
    }

    public void insert(int i, Key key) {
        keys[i] = key;
        pq[++N] = i;
        qp[i] = N;
        swim(N);
    }

    public void changeKey(int i, Key key) {
        keys[i] = key;
        swim(qp[i]);
        sink(qp[i]);
    }

    public boolean contains(int i) {
        return qp[i] != -1;
    }

    public void delete(int i) {
        pq[qp[i]] = pq[N--];
        swimSink(qp[i]);
        qp[i] = -1;
    }

    public Key minKey() {
        return keys[pq[1]];
    }

    public int minIndex() {
        return pq[1];
    }

    public Key delMin() {
        Key max = keys[pq[1]];
        exch(1, N--);
        sink(1);
        qp[pq[N+1]] = -1;
        return max;
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public Key keyOf(int i) {
        return keys[i];
    }

    private void swim(int k) {
        while (k != 1 && less(k/2, k)) {
            exch(k/2, k);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && less(j, j+1)) ++j;
            if (less(j, k)) break;
            exch(j, k);
            k = j;
        }
    }

    private boolean less(int i, int j) {
        return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    private void exch(int i, int j) {
        int t = pq[i];
        pq[i] = pq[j];
        pq[j] = t;

        qp[pq[i]] = i;
        qp[pq[j]] = j;
    }

    private void swimSink(int k) {
        swim(k);
        sink(k);
    }
}
