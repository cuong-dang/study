package com.cuongd.study.algsp1.book.ch3;

/** Subtree count check */
public class Ex3232<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    public Ex3232() {
        super();
    }

    public boolean checkCount() {
        return checkCount(root);
    }

    private boolean checkCount(Node x) {
        if (x.left == null && x.right == null) return x.n == 1;
        if (x.left == null) return checkCount(x.right) && x.n == x.right.n + 1;
        if (x.right == null) return checkCount(x.left) && x.n == x.left.n + 1;
        return checkCount(x.left) && checkCount(x.right) && x.n == x.left.n + x.right.n + 1;
    }
}
