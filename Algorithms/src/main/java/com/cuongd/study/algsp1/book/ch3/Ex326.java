package com.cuongd.study.algsp1.book.ch3;

public class Ex326<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    private Node root2;

    public int height1() {
        return height1(root);
    }

    private int height1(BST<Key, Value>.Node x) {
        if (x == null) return 0;
        int heightLeft = height1(x.left), heightRight = height1(x.right);
        return heightLeft > heightRight ? 1 + heightLeft : 1 + heightRight;
    }

    @Override
    public void put(Key key, Value val) {
        super.put(key, val);
        root2 = put(root2, key, val);
    }

    private Node put(Node x, Key key, Value val) {
        if (x == null) return new Node(key, val, 1, 1);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = put(x.left, key, val);
        else if (cmp > 0) x.right = put(x.right, key, val);
        else x.val = val;
        x.n = size(x.left) + size(x.right) + 1;
        x.h = height2(x.left) > height2(x.right) ? height2(x.left) + 1 : height2(x.right) + 1;
        return x;
    }

    public int height2() {
        if (root2 == null) return 0;
        return root2.h;
    }

    private int height2(Node x) {
        if (x == null) return 0;
        return x.h;
    }

    private class Node extends BST<Key, Value>.Node {
        private int h;
        private Node left, right;

        public Node(Key key, Value val, int n, int h) {
            super(key, val, n);
            this.h = h;
        }
    }
}
