package com.cuongd.study.algsp1.book.ch3;

import edu.princeton.cs.algs4.StdRandom;

import java.util.NoSuchElementException;

public class Ex3221<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    public Key randomKey() {
        if (isEmpty()) throw new NoSuchElementException();
        return randomKey(root, 1.0 / size(root));
    }

    private Key randomKey(Node x, double p) {
        if (x.left == null && x.right == null) return x.key;
        if (StdRandom.uniform() < p) return x.key;
        if (x.left == null) return randomKey(x.right, 1.0 / size(x.right));
        if (x.right == null) return randomKey(x.left, 1.0 / size(x.left));
        double leftWeight = size(x.left) / (size(x) - 1.0);
        if (StdRandom.uniform() < leftWeight) return randomKey(x.left, 1.0 / size(x.left));
        return randomKey(x.right, 1.0 / size(x.right));
    }
}
