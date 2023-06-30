package com.cuongd.study.algs.book.ch1;

import java.lang.reflect.InvocationTargetException;

/** Weighted quick-union by height */
public class Ex1514 extends UnionFindQuickUnion {
    private final int[] ht;

    public Ex1514(int N) {
        super(N);
        ht = new int[N];
        for (int i = 0; i < N; i++) {
            ht[i] = 0;
        }
    }

    @Override
    public void union(int p, int q) {
        int i = find(p);
        int j = find(q);

        if (i == j) {
            return;
        }
        if (ht[i] < ht[j]) {
            id[i] = j;
            ht[j] += 1;
        } else {
            id[j] = i;
            ht[i] += 1;
        }
        count--;
    }

    public static void main(String[] args) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        run(Ex1514.class);
    }
}
