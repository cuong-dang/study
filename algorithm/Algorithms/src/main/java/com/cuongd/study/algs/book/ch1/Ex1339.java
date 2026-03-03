package com.cuongd.study.algs.book.ch1;

public class Ex1339<Item> {
    private static final int RING_SIZE = 4;

    private Item[] a;
    private int head;
    private int tail;
    private int N;

    public Ex1339() {
        a = (Item[]) new Object[RING_SIZE];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void put(Item item) {
        if (isFull()) {
            throw new IllegalStateException("Ring is full");
        }
        a[head] = item;
        head = next(head);
        N++;
    }

    public Item get() {
        if (isEmpty()) {
            throw new IllegalStateException("Ring is empty");
        }
        Item retVal = a[tail];
        tail = next(tail);
        N--;
        return retVal;
    }

    private int next(int index) {
        return (index + 1) % RING_SIZE;
    }

    private boolean isFull() {
        return N == RING_SIZE;
    }
}
