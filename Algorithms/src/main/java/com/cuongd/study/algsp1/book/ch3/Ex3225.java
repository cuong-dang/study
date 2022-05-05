package com.cuongd.study.algsp1.book.ch3;

import java.util.Arrays;

public class Ex3225<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    public Ex3225(Item<Key, Value>[] items) {
        Arrays.sort(items);
        root = binaryPut(items, 0, items.length - 1);
    }

    private Node binaryPut(Item<Key, Value>[] items, int lo, int hi) {
        if (lo > hi) return null;
        int mid = lo + (hi - lo) / 2;
        Node x = new Node(items[mid].key, items[mid].val, 1);
        x.left = binaryPut(items, lo, mid-1);
        x.right = binaryPut(items, mid+1, hi);
        x.n = size(x.left) + size(x.right) + 1;
        return x;
    }

    public static class Item<Key extends Comparable<Key>, Value> implements
            Comparable<Item<Key, Value>> {
        Key key;
        Value val;

        public Item(Key key, Value val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public int compareTo(Item o) {
            //noinspection unchecked
            return key.compareTo((Key) o.key);
        }
    }
}
