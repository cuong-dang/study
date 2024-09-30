package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.Queue;

public class Ex517 {
    private KeyIndex[] a;

    public Ex517(KeyIndex[] a, int R) {
        this.a = new KeyIndex[a.length];
        int N = a.length;
        Queue<KeyIndex>[] indices = new Queue[R];
        for (int r = 0; r < R; r++) {
            indices[r] = new Queue<>();
        }
        for (KeyIndex kv : a) {
            indices[kv.key].enqueue(kv);
        }
        int i = 0;
        for (int r = 0; r < R; r++) {
            while (!indices[r].isEmpty()) {
                this.a[i++] = indices[r].dequeue();
            }
        }
    }

    public void show() {
        for (KeyIndex kv : a) {
            System.out.printf("%-10s%d\n", kv.val, kv.key);
        }
    }

    public static void main(String[] args) {
        new KeyIndexedCounting(new KeyIndex[]{
                new KeyIndex(2, "Anderson"),
                new KeyIndex(3, "Brown"),
                new KeyIndex(3, "Davis"),
                new KeyIndex(4, "Garcia"),
                new KeyIndex(1, "Harris"),
                new KeyIndex(3, "Jackson"),
                new KeyIndex(4, "Johnson"),
                new KeyIndex(3, "Jones"),
                new KeyIndex(1, "Martin"),
                new KeyIndex(2, "Martinez"),
                new KeyIndex(2, "Miller"),
                new KeyIndex(1, "Moore"),
                new KeyIndex(2, "Robinson"),
                new KeyIndex(4, "Smith"),
                new KeyIndex(3, "Taylor"),
                new KeyIndex(4, "Thomas"),
                new KeyIndex(4, "Thompson"),
                new KeyIndex(2, "White"),
                new KeyIndex(3, "Williams"),
                new KeyIndex(4, "Wilson"),
        }, 5).show();
    }
}
