package com.cuongd.study.algs.book.ch5;

import java.util.Arrays;

public class KeyIndexedCounting {
    private KeyIndex[] a;

    public KeyIndexedCounting(KeyIndex[] a, int R) {
        this.a = new KeyIndex[a.length];
        int N = a.length;
        int[] count = new int[R+1];
        Arrays.fill(count, 0);

        for (KeyIndex kv : a) {
            count[kv.key+1]++;
        }
        for (int r = 0; r < R; r++) {
            count[r+1] += count[r];
        }
        for (int i = 0; i < N; i++) {
            this.a[count[a[i].key]++] = a[i];
        }
    }

    public void show() {
        for (KeyIndex kv : a) {
            System.out.printf("%-10s%d\n", kv.val, kv.key);
        }
    }

    public static class KeyIndex {
        String val;
        int key;

        public KeyIndex(int key, String val) {
            this.key = key;
            this.val = val;
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
