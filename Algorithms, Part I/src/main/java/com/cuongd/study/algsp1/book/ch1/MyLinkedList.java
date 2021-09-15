package com.cuongd.study.algsp1.book.ch1;

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

        Node prev = first, curr = first;
        while (k > 0) {
            k--;
            prev = curr;
            curr = curr.next;
        }
        prev.next = curr.next;
    }

    class Node {
        Item value;
        Node next;
    }
}
