package com.cuongd.study.algs.book.ch1;

public class MyLinkedList<Item> {
    public Node first;
    private int N;

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void add(Item item) {
        Node rest = first;
        first = new Node();
        first.value = item;
        first.next = rest;
        N++;
    }

    /** Delete the k-th element. Assume it exists. */
    public void delete(int k) {
        N--;
        if (k == 0) {
            first = first.next;
            return;
        }

        Node curr = first;
        while (k > 1) {
            k--;
            curr = curr.next;
        }
        curr.next = curr.next.next;
    }

    public void removeAfter(Node node) {
        if (node == null) {
            return;
        }
        for (Node curr = first; curr != null; curr = curr.next) {
            if (curr == node && curr.next != null) {
                N--;
                curr.next = curr.next.next;
            }
        }
    }

    class Node {
        Item value;
        Node next;
    }
}
