package com.cuongd.study.algs.book.ch3;

import java.util.List;

/** BST reconstruction */
public class Ex3230<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    public Ex3230(List<Ex3230Node<Key, Value>> nodes) {
        assert(nodes.size() != 0);
        root = constructPreorder(nodes, 0, nodes.size());
    }

    private Node constructPreorder(List<Ex3230Node<Key, Value>> nodes, int start, int end) {
        if (start == end) {
            return null;
        }
        Node tree = new Node(nodes.get(start).key, nodes.get(start).val, 1);
        int forkIndex = findForkIndex(nodes, start, end);
        if (forkIndex != -1) {
            tree.left = constructPreorder(nodes, start + 1, forkIndex);
            tree.right = constructPreorder(nodes, forkIndex, end);
            tree.n = size(tree.left) + size(tree.right) + 1;
        }
        return tree;
    }

    private int findForkIndex(List<Ex3230Node<Key, Value>> nodes, int start, int end) {
        if (start == end) {
            return -1;
        }
        int forkIndex;
        for (forkIndex = start + 1; forkIndex < end; ++forkIndex) {
            if (nodes.get(start).key.compareTo(nodes.get(forkIndex).key) < 0) {
                break;
            }
        }
        return forkIndex;
    }

    public static class Ex3230Node<Key extends Comparable<Key>, Value> {
        public Key key;
        public Value val;

        public Ex3230Node(Key key, Value val) {
            this.key = key;
            this.val = val;
        }
    }
}
