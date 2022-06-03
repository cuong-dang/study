package com.cuongd.study.algsp1.book.ch3;

/** Certification */
public class Ex3231<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    public Ex3231() {
        super();
    }

    public boolean isBST() {
        return isBST(root, null, null);
    }

    private boolean isBST(Node x, Key min, Key max) {
        if (x == null) return true;
        if (min != null && x.key.compareTo(min) <= 0) return false;
        if (max != null && x.key.compareTo(max) >= 0) return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    }
}
