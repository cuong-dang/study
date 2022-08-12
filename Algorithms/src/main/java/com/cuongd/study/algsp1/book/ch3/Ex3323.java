package com.cuongd.study.algsp1.book.ch3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ex3323<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private Node root;

    @Override
    public void put(Key key, Value val) {
        root = put(root, key, val);
        root.color = BLACK;
    }

    private Node put(Node h, Key key, Value val) {
        if (h == null) return new Node(key, val, 1, RED);
        int cmp = key.compareTo(h.key);
        if (cmp < 0) h.left = put(h.left, key, val);
        else if (cmp > 0) h.right = put(h.right, key, val);
        else h.val = val;

        if (isRed(h) && isRed(h.left)) h.left.color = BLACK;
        if (isRed(h) && isRed(h.right)) h.right.color = BLACK;

        h.n = size(h.left) + size(h.right) + 1;
        return h;
    }

    private class Node extends BST<Key, Value>.Node {
        private Node left, right;
        private boolean color;

        public Node(Key key, Value val, int n, boolean color) {
            super(key, val, n);
            this.color = color;
        }
    }

    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    public static void main(String[] args) {
        int N_MAX = 16_780_096, N_START = 1;
        String sep = "\t\t";

        System.out.printf("%10s%savgPathLength%n", "n", sep);
        for (int i = N_START; i <= N_MAX; i *= 2) {
            List<Integer> keys = createShuffledKeys(i);
            Ex3323<Integer, Integer> st = new Ex3323<>();
            for (Integer key : keys) {
                st.put(key, 0);
            }
            double avgPathLength = calculateAveragePathLength(st);
            System.out.printf("%10d%s%13.2f%n", i, sep, avgPathLength);
        }
    }

    private static List<Integer> createShuffledKeys(int length) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            result.add(i);
        }
        Collections.shuffle(result);
        return result;
    }

    private static <T extends Comparable<T>, U> double calculateAveragePathLength(Ex3323<T, U> st) {
        List<Integer> lengths = new ArrayList<>();
        walk(lengths, st.root, 0);

        double sum = 0;
        for (Integer length : lengths) {
            sum += length;
        }
        return sum / lengths.size();
    }

    private static <T extends Comparable<T>, U> void walk(List<Integer> lengths, Ex3323<T, U>.Node x, int currLength) {
        if (x.left == null && x.right == null) {
            lengths.add(currLength);
            return;
        }
        if (x.left != null) {
            walk(lengths, x.left, x.left.color == BLACK ? currLength + 1 : currLength);
        }
        if (x.right != null) {
            walk(lengths, x.right, x.right.color == BLACK ? currLength + 1 : currLength);
        }
    }
}
