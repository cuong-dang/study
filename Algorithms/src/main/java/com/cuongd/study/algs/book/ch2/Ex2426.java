package com.cuongd.study.algs.book.ch2;

public class Ex2426<Key extends Comparable<Key>> extends MaxPQ<Key> {
    @Override
    protected void swim(int k) {
        Key t = pq[k];
        while (k != 1 && less(k/2, k)) {
            pq[k] = pq[k/2];
            pq[k/2] = t;
            k = k/2;
        }
    }

    @Override
    protected void sink(int k) {
        Key t = pq[k];
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && less(j, j+1)) ++j;
            if (less(j, k)) break;
            pq[k] = pq[j];
            pq[j] = t;
            k = j;
        }
    }
}
