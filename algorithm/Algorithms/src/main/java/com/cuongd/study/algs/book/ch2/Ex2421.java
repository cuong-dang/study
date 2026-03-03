package com.cuongd.study.algs.book.ch2;

public class Ex2421<Key> {
    MaxPQ<Box<Key>> pq;
    private int currWeight;

    public Ex2421(int maxN) {
        pq = new MaxPQ<>(maxN);
    }

    public Ex2421() {
        pq = new MaxPQ<>();
    }

    public void push(Key item) {
        Box<Key> box = new Box<>(item, currWeight++);
        pq.insert(box);
    }

    public Key pop() {
        return pq.delMax().item;
    }

    public int capacity() {
        return pq.capacity();
    }

    private static class Box<Key> implements Comparable<Box<Key>> {
        Key item;
        int weight;

        public Box(Key item, int weight) {
            this.item = item;
            this.weight = weight;
        }

        @Override
        public int compareTo(Box<Key> o) {
            return weight - o.weight;
        }
    }
}
