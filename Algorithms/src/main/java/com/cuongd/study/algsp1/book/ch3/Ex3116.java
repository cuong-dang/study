package com.cuongd.study.algsp1.book.ch3;

public class Ex3116<Key extends Comparable<Key>, Value> extends BinarySearchST<Key, Value> {
    public Ex3116(int cap) {
        super(cap);
    }

    public void delete(Key key) {
        int i = rank(key);
        if (key.compareTo(keys[i]) == 0) {
            for (int j = i; j < n-1; ++j) {
                keys[j] = keys[j+1];
                vals[j] = vals[j+1];
            }
            --n;
        }
    }
}
