package com.cuongd.study.algsp1.coursera.wk2;

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node first;
    private Node last;
    private int size;

    public Deque() { }

    public boolean isEmpty() { return size == 0; }

    public int size() { return size; }

    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException();

        Node newFirst = new Node();
        newFirst.item = item;
        newFirst.next = first;
        newFirst.prev = null;
        if (first != null)
            first.prev = newFirst;
        first = newFirst;

        if (last == null)
            last = first;
        ++size;
    }

    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException();

        Node oldLast = last;
        Node newLast = new Node();
        newLast.item = item;
        newLast.next = null;
        newLast.prev = oldLast;
        if (oldLast != null)
            oldLast.next = newLast;
        last = newLast;

        if (first == null)
            first = last;
        ++size;
    }

    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();

        Item item = first.item;
        if (first.next != null)
            first.next.prev = null;
        else // only 1 item in deque
            last = null;
        first = first.next;
        --size;
        return item;
    }

    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException();

        Item item = last.item;
        if (last.prev != null)
            last.prev.next = null;
        else // only 1 item in deque
            first = null;
        last = last.prev;
        --size;
        return item;
    }

    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class Node {
        Item item;
        Node next;
        Node prev;
    }

    private class DequeIterator implements Iterator<Item> {
        private Node cursor = first;

        @Override
        public boolean hasNext() { return cursor != null; }

        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();

            Item item = cursor.item;
            cursor = cursor.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static void main(String[] args) {
        Deque<Integer> d = new Deque<>(); assert d.isEmpty(); assert d.size() == 0;

        d.addFirst(1); assert !d.isEmpty(); assert d.size() == 1;
        int i = d.removeFirst(); assert i == 1; assert d.isEmpty(); assert d.size() == 0;

        d.addFirst(2);
        i = d.removeLast(); assert i == 2; assert d.isEmpty(); assert d.size() == 0;

        d.addLast(3);
        i = d.removeLast(); assert i == 3;
        d.addLast(4);
        i = d.removeFirst(); assert i == 4;

        d.addFirst(1); d.addFirst(2); d.addFirst(3);
        i = d.removeFirst(); assert i == 3;
        i = d.removeLast(); assert i == 1;

        d.addFirst(1); d.addFirst(2); d.addLast(3); d.addLast(4); d.addFirst(5);
        for (Integer item : d) StdOut.println(item);
        i = d.removeFirst(); assert i == 5;
        i = d.removeLast(); assert i == 4;
        i = d.removeLast(); assert i == 3;
        i = d.removeFirst(); assert i == 2;
        i = d.removeLast(); assert i == 1;
        assert d.isEmpty(); assert d.size() == 0;
    }
}
