package com.cuongd.study.algs.book.ch2;

public class MinPQ<Key extends Comparable<Key>> extends MaxPQ<Key> {
    public MinPQ() {
        super();
    }

    @Override
    protected boolean less(int i, int j) {
        return super.less(j, i);
    }
}

