package com.cuongd.study.algsp1.book.ch1;

public class MyLinkedList<Item> {
    public Node first;

    public boolean isEmpty() {
        return first == null;
    }

    public void add(Item item) {
        Node rest = first;
        first = new Node();
        first.value = item;
        first.next = rest;
    }

    class Node {
        Item value;
        Node next;
    }
}
