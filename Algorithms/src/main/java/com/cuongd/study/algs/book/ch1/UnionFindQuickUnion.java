package com.cuongd.study.algs.book.ch1;

import java.lang.reflect.InvocationTargetException;

public class UnionFindQuickUnion extends UnionFind {
    public UnionFindQuickUnion(int N) {
        super(N);
    }

    @Override
    public int find(int p) {
        while (id[p] != p) {
            p = id[p];
        }
        return p;
    }

    @Override
    public void union(int p, int q) {
        int i = find(p);
        int j = find(q);

        if (i == j) {
            return;
        }
        id[i] = j;
        count--;
    }

    public static void main(String[] args) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        run(UnionFindQuickUnion.class);
    }
}
