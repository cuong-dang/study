package com.cuongd.study.algs.book.ch1;

/** Queue with two stacks */
public class Ex1427<Item> {
    private final MyStack<Item> enq;
    private final MyStack<Item> deq;

    public Ex1427() {
        enq = new MyStack<>();
        deq = new MyStack<>();
    }

    public boolean isEmpty() {
        return enq.isEmpty() && deq.isEmpty();
    }

    public void enqueue(Item item) {
        enq.push(item);
    }

    public Item dequeue() {
        if (deq.isEmpty()) {
            while (!enq.isEmpty()) {
                Item item = enq.pop();
                deq.push(item);
            }
        }
        return deq.pop();
    }
}
