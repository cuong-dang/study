package com.cuongd.study.algs.book.ch1;

@SuppressWarnings("unchecked")
public class MyQueue<Item> {
    private static final int INIT_SZ = 2;

    private Item[] a;
    private int N;
    private int head;
    private int tail;

    public MyQueue() {
        a = (Item[]) new Object[INIT_SZ];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void enqueue(Item item) {
        if (N == a.length) {
            resize(2 * N);
        } else if (tail == a.length) {
            moveToFront();
        }
        a[tail++] = item;
        N++;
    }

    public Item dequeue() {
        if (N > 0 && N == a.length / 4) {
            moveToFront();
            resize(a.length / 2);
        }
        N--;
        return a[head++];
    }

    private void resize(int newSize) {
        Item[] newA = (Item[]) new Object[newSize];
        System.arraycopy(a, 0, newA, 0, N);
        a = newA;
    }

    /** Move queue items to front. */
    private void moveToFront() {
        Item[] newA = (Item[]) new Object[a.length];
        System.arraycopy(a, head, newA, 0, N);
        a = newA;
        head = 0;
        tail = N;
    }
}
