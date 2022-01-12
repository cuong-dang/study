package com.cuongd.study.algsp1.book.ch2;

import java.util.ArrayList;
import java.util.List;

public class Ex2424<Key extends Comparable<Key>> {
    private final Node<Key> root;
    private int nextInsertId;

    public Ex2424() {
        root = new Node<>();
        root.positionId = 1;
        nextInsertId = 1;
    }

    public void insert(Key value) {
        Node<Key> nextInsert = getNextInsert();
        nextInsert.value = value;
        nextInsert.makeChildren();
        swim(nextInsert);
    }

    public Key delMax() {
        Key max = root.value;
        Node<Key> lastNode = getNode(--nextInsertId);
        exchangeValue(root, lastNode);
        sink();
        return max;
    }

    private void swim(Node<Key> node) {
        while (node.up != null && less(node.up, node)) {
            exchangeValue(node, node.up);
            node = node.up;
        }
    }

    private boolean less(Node<Key> x, Node<Key> y) {
        return x.value.compareTo(y.value) < 0;
    }

    private void exchangeValue(Node<Key> x, Node<Key> y) {
        Key t = x.value;
        x.value = y.value;
        y.value = t;
    }

    private Node<Key> getNextInsert() {
        int nextPositionId = nextInsertId++;
        return getNode(nextPositionId);
    }

    private Node<Key> getNode(int positionId) {
        List<LeftRight> pathToRoot = buildPathToRoot(positionId);
        return getNode(pathToRoot);
    }

    private Node<Key> getNode(List<LeftRight> pathToRoot) {
        Node<Key> result = root;
        int pathLength = pathToRoot.size();
        for (int i = pathLength - 1; i >= 0; --i) {
            if (pathToRoot.get(i) == LeftRight.LEFT)
                result = result.left;
            else
                result = result.right;
        }
        return result;
    }

    private List<LeftRight> buildPathToRoot(int positionId) {
        List<LeftRight> path = new ArrayList<>();
        while (positionId > 1) {
            if (positionId % 2 == 0)
                path.add(LeftRight.LEFT);
            else
                path.add(LeftRight.RIGHT);
            positionId /= 2;
        }
        return path;
    }

    private void sink() {
        Node<Key> curr = root;
        while (2*curr.positionId < nextInsertId) { // node has children
            Node<Key> exchangeNode = curr.right.positionId < nextInsertId
                    ? less(curr.left, curr.right) ? curr.right : curr.left
                    : curr.left;
            if (less(exchangeNode, curr)) {
                return;
            }
            exchangeValue(curr, exchangeNode);
            curr = exchangeNode;
        }
    }

    private static class Node<Key> {
        Key value;
        int positionId;
        Node<Key> left;
        Node<Key> right;
        Node<Key> up;

        public void makeChildren() {
            left = new Node<>();
            left.up = this;
            left.positionId = 2*positionId;
            right = new Node<>();
            right.up = this;
            right.positionId = 2*positionId + 1;
        }
    }

    private enum LeftRight {
        LEFT,
        RIGHT
    }
}
