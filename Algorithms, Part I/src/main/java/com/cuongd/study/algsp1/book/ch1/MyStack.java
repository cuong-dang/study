package com.cuongd.study.algsp1.book.ch1;

import java.util.Iterator;

@SuppressWarnings("unchecked")
public class MyStack<Item> implements Iterable<Item> {
    private static final int INIT_SZ = 2;

    private int N;
    private Item[] a;

    public MyStack() {
        a = (Item []) new Object[INIT_SZ];
    }

    public void push(Item item) {
        if (N == a.length)
            resize(2 * N);
        a[N++] = item;
    }

    public Item pop() {
        Item popped = a[--N];
        if (N == a.length / 4)
            resize(a.length / 2);
        return popped;
    }

    public int size() {
        return N;
    }

    public boolean isEmpty() {
        return N == 0;
    }

    @Override
    public Iterator<Item> iterator() {
        return new MyStackIterator();
    }

    private void resize(int newSize) {
        Item[] newA = (Item[]) new Object[newSize];
        System.arraycopy(a, 0, newA, 0, N);
        a = newA;
    }

    private class MyStackIterator implements Iterator<Item> {
        private int i = N;

        @Override
        public boolean hasNext() {
            return i > 0;
        }

        @Override
        public Item next() {
            return a[--i];
        }
    }
}
