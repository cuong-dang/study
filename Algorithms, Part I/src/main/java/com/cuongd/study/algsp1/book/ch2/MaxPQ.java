package com.cuongd.study.algsp1.book.ch2;

@SuppressWarnings("unchecked")
public class MaxPQ<Key extends Comparable<Key>> {
    private static final int INIT_SIZE = 4;

    protected Key[] pq;
    protected int N = 0;
    private Key min;

    public MaxPQ() {
        pq = (Key[]) new Comparable[INIT_SIZE];
    }

    public MaxPQ(int maxN) {
        pq = (Key[]) new Comparable[maxN+1];
    }

    public MaxPQ(Key[] elems) {
        N = elems.length;
        pq = (Key[]) new Comparable[N+1];
        System.arraycopy(elems, 0, pq, 1, N);
        for (int k = N/2; k >= 1; k--) {
            sink(k);
        }
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void insert(Key v) {
        if (N == pq.length-1)
            grow();
        pq[++N] = v;
        if (N == 1)
            min = v;
        else if (min.compareTo(v) > 0)
            min = v;
        swim(N);
    }

    public Key delMax() {
        Key max = pq[1];
        exch(1, N--);
        pq[N+1] = null;
        sink(1);
        if (N > 0 && N*4 == pq.length)
            shrink();
        return max;
    }

    public Key peek() {
        return pq[1];
    }

    public Key min() {
        return min;
    }

    public int capacity() {
        return pq.length;
    }

    protected boolean less(int i, int j) {
        return pq[i].compareTo(pq[j]) < 0;
    }

    private void exch(int i, int j) {
        Key t = pq[i];
        pq[i] = pq[j];
        pq[j] = t;
    }

    protected void swim(int k) {
        while (k != 1 && less(k/2, k)) {
            exch(k/2, k);
            k = k/2;
        }
    }

    protected void sink(int k) {
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && less(j, j+1)) ++j;
            if (less(j, k)) break;
            exch(j, k);
            k = j;
        }
    }

    private void grow() {
        resize(pq.length*2, pq.length-1);
    }

    private void shrink() {
        resize(pq.length/2, N);
    }

    private void resize(int newSize, int copyLength) {
        Key[] newPq = (Key[]) new Comparable[newSize];
        System.arraycopy(pq, 1, newPq, 1, copyLength);
        pq = newPq;
    }
}
