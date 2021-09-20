package com.cuongd.study.algsp1.book.ch1;

import static edu.princeton.cs.algs4.StdOut.printf;
import static edu.princeton.cs.algs4.StdOut.println;

public class Ex1337 {
    private static final int M = 2;
    private static final int N = 7;

    public static void main(String[] args) {
        MyQueue<Integer> queue = new MyQueue<>();
        fillQueue(queue);
        while (queue.size() != 1) {
            for (int i = 1; i < M; i++) {
                queue.enqueue(queue.dequeue());
            }
            printf("%d ", queue.dequeue());
        }
        println(queue.dequeue());
    }

    private static void fillQueue(MyQueue<Integer> q) {
        for (int i = 0; i < N; i++) {
            q.enqueue(i);
        }
    }
}
