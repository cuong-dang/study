package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;

public class Ex5111 {
    private static int R = 256;

    public static void sort(String[] a) {
        Queue<String> q = new Queue<>();
        for (String s : a) {
            q.enqueue(s);
        }
        sort(q, 0);
        for (int i = 0; i < a.length; i++) {
            a[i] = q.dequeue();
        }
    }

    private static void sort(Queue<String> q, int d) {
        if (q.size() < 2) return;
        Queue<String>[] qs = new Queue[R + 1];
        for (int i = 0; i < qs.length; i++) {
            qs[i] = new Queue<>();
        }
        while (!q.isEmpty()) {
            String s = q.dequeue();
            qs[MSD.charAt(s, d) + 1].enqueue(s);
        }
        for (int i = 1; i < qs.length; i++) {
            sort(qs[i], d+1);
        }
        for (Queue<String> q_ : qs) {
            while (!q_.isEmpty()) {
                q.enqueue(q_.dequeue());
            }
        }
    }

    public static void main(String[] args) {
        String[] b = new String[]{"she", "sells", "seashells", "by", "the",
                "seashore", "the", "shells", "she", "sells", "are", "surely",
                "seashells"};
        Ex5111.sort(b);
        System.out.println(Arrays.toString(b));
    }
}
