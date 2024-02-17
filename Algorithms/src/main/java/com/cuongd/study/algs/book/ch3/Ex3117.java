package com.cuongd.study.algs.book.ch3;

public class Ex3117<Key extends Comparable<Key>, Value> extends BinarySearchST<Key, Value> {
    public Ex3117(int cap) {
        super(cap);
    }

    public Key floor(Key key) {
        int i = rank(key);
        if (i < n && key.compareTo(keys[i]) == 0)
            return keys[i];
        if (i > 0)
            return keys[i - 1];
        return null;
    }
}