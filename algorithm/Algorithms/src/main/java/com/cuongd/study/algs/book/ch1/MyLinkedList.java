package com.cuongd.study.algs.book.ch1;

public class MyLinkedList<E> {
    public Node first;
    private int N;

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public E elemAt(int k) {
        for (Node x = first; x != null; x = x.next) {
            if (k == 0) return x.value;
            k--;
        }
        return null;
    }

    public int rank(E e) {
        int i = 0;
        for (Node x = first; x != null; x = x.next) {
            if (x.value.equals(e)) return i;
            i++;
        }
        return -1;
    }

    public void add(E e) {
        Node rest = first;
        first = new Node();
        first.value = e;
        first.next = rest;
        N++;
    }

    /**
     * Delete the k-th element. Assume it exists.
     */
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

    public class Node {
        E value;
        Node next;
    }
}
