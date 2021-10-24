package com.cuongd.study.algsp1.coursera.wk2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int INIT_SIZE = 2;

    private Item[] items;
    private int[] indexes;
    private int size;
    private boolean indexesShuffled;

    public RandomizedQueue() {
        items = (Item[]) new Object[INIT_SIZE];
        indexes = new int[INIT_SIZE];
        for (int i = 0; i < INIT_SIZE; ++i)
            indexes[i] = i;
    }

    public boolean isEmpty() { return size == 0; }

    public int size() { return size; }

    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException();

        if (size == items.length)
            resize(2 * items.length);
        items[indexes[size++]] = item;
        indexesShuffled = false;
    }

    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException();

        if (!indexesShuffled) {
            StdRandom.shuffle(indexes, 0, size);
            indexesShuffled = true;
        }
        Item item = items[indexes[--size]];
        if (size > 0 && size == items.length / 4)
            resize(items.length / 2);
        return item;
    }

    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException();

        int randomIndex = indexes[StdRandom.uniform(0, size)];
        return items[randomIndex];
    }

    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private void resize(int newSize) {
        Item[] newItems = (Item[]) new Object[newSize];
        int[] newIndexes = new int[newSize];
        int copySize = newSize < size ? newSize : size;
        /* copy old items */
        for (int i = 0; i < copySize; ++i) {
            newItems[i] = items[indexes[i]];
        }
        /* copy indexes */
        if (items.length < newSize) { // grow
            for (int i = 0; i < copySize; ++i)
                newIndexes[i] = indexes[i]; // copy old indexes
            for (int i = size; i < newSize; ++i)
                newIndexes[i] = i; // set new indexes
        } else { // shrink
            for (int i = 0; i < copySize; ++i)
                newIndexes[i] = i; // set all new indexes
            StdRandom.shuffle(newIndexes, 0, size); // only shuffle existing items' indexes
        }
        items = newItems;
        indexes = newIndexes;
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private final int[] iteratorIndexes;
        private int cursor;

        public RandomizedQueueIterator() {
            iteratorIndexes = new int[size];
            for (int i = 0; i < size; i++)
                iteratorIndexes[i] = indexes[i];
            StdRandom.shuffle(iteratorIndexes);
        }

        @Override
        public boolean hasNext() { return cursor != size; }

        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();

            return items[iteratorIndexes[cursor++]];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        assert q.isEmpty(); assert q.size() == 0;

        q.enqueue(1); assert !q.isEmpty(); assert q.size() == 1;
        int n = q.dequeue(); assert n == 1; assert q.isEmpty(); assert q.size() == 0;

        for (int i = 0; i < 10; ++i) q.enqueue(i);
        for (int i : q) StdOut.printf("%d ", i);
        StdOut.println();
        for (int i = 0; i < 10; ++i) StdOut.printf("%d ", q.dequeue());
        StdOut.println();

        for (int i = 0; i < 10; ++i) q.enqueue(i);
        for (int i = 0; i < 5; ++i) StdOut.printf("%d ", q.dequeue());
        StdOut.println();
        for (int i = 0; i < 5; ++i) q.enqueue(i + 10);
        for (int i = 0; i < 10; ++i) StdOut.printf("%d ", q.dequeue());
        StdOut.println();

        for (int i = 0; i < 10; ++i) q.enqueue(i);
        for (int i = 0; i < 10; ++i) StdOut.printf("%d ", q.sample());
        StdOut.println();
    }
}
