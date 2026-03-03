package com.cuongd.study.algs.book.ch5;

import java.util.Arrays;

public class Ex5117 {
    public Ex5117(KeyIndex[] a, int R) {
        int N = a.length;
        int[] count = new int[R+1];
        int[] marker = new int[R+1];
        Arrays.fill(count, 0);

        for (KeyIndex kv : a) {
            count[kv.key+1]++;
        }
        for (int r = 0; r < R; r++) {
            count[r+1] += count[r];
        }
        System.arraycopy(count, 0, marker, 0, R+1);

        boolean needsRechecking = true;
        while(needsRechecking) {
            needsRechecking = false;
            for (int i = 0; i < N; i++) {
                if (marker[a[i].key] <= i && i < marker[a[i].key+1]) {
                    continue;
                }
                swap(a, i, count[a[i].key]++);
                needsRechecking = true;
            }
        }

        for (KeyIndex kv : a) {
            System.out.printf("%-10s%d\n", kv.val, kv.key);
        }
    }

    private static void swap(KeyIndex[] a, int i, int j) {
        KeyIndex t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static void main(String[] args) {
        new Ex5117(new KeyIndex[]{
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
        }, 5);
    }
}
