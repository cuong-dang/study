package com.cuongd.study.algsp1.book.ch3;

public class Ex3325<Key extends Comparable<Key>, Value> extends RedBlackBST<Key, Value> {
    @Override
    protected RedBlackBST<Key, Value>.Node put(RedBlackBST<Key, Value>.Node h, Key key, Value val) {
        if (h == null) return new Node(key, val, 1, RED);

        if (isRed(h.left) && isRed(h.right)) flipColors(h);

        int cmp = key.compareTo(h.key);
        if (cmp < 0) h.left = put(h.left, key, val);
        else if (cmp > 0) h.right = put(h.right, key, val);
        else h.val = val;

        if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);

        h.n = size(h.left) + size(h.right) + 1;
        return h;
    }
}
