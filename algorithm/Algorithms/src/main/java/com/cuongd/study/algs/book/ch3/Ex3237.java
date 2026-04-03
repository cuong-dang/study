package com.cuongd.study.algs.book.ch3;

import edu.princeton.cs.algs4.Queue;

/** Level-order traversal */
public class Ex3237<Key extends Comparable<Key>, Value> extends BST<Key, Value> {
    public Iterable<Key> printLevel() {
        Queue<Node> nodeQueue = new Queue<>();
        Queue<Key> result = new Queue<>();
        nodeQueue.enqueue(root);
        while (!nodeQueue.isEmpty()) {
            Node x = nodeQueue.dequeue();
            result.enqueue(x.key);
            if (x.left != null) nodeQueue.enqueue(x.left);
            if (x.right != null) nodeQueue.enqueue(x.right);
        }
        return result;
    }
}
