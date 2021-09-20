package com.cuongd.study.algsp1.book.ch1;

/* Only implement the ResizingArrayDeque. */
@SuppressWarnings("unchecked")
public class Ex1333<Item> {
    private static final int INIT_SIZE = 4;
    private static final int INIT_LEFT_INDEX = 1;
    private static final int INIT_RIGHT_INDEX = 2;

    private Item[] a;
    private int N;
    private int leftIndex = INIT_LEFT_INDEX, rightIndex = INIT_RIGHT_INDEX;

    public Ex1333() {
        a = (Item[]) new Object[INIT_SIZE];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void pushLeft(Item item) {
        if (leftIndex == -1) {
            resize(2 * a.length);
        }
        a[leftIndex--] = item;
        N++;
    }

    public void pushRight(Item item) {
        if (rightIndex == a.length) {
            resize(2 * a.length);
        }
        a[rightIndex++] = item;
        N++;
    }

    public Item popLeft() {
        N--;
        return a[++leftIndex];
    }

    public Item popRight() {
        N--;
        return a[--rightIndex];
    }

    private void resize(int newSize) {
        Item[] newA = (Item[]) new Object[newSize];
        int newLeftIndex = (newSize - a.length)/2;
        System.arraycopy(a, leftIndex+1, newA, newLeftIndex+1, N);
        leftIndex = newLeftIndex;
        rightIndex = newLeftIndex + N + 1;
        a = newA;
    }
}
