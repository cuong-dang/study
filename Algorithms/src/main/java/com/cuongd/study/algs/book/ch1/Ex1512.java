package com.cuongd.study.algs.book.ch1;

import java.lang.reflect.InvocationTargetException;

/** Quick-union with path compression */
public class Ex1512 extends UnionFindQuickUnion {
    public Ex1512(int N) {
        super(N);
    }

    @Override
    public int find(int p) {
        int pp = p;

        while (p != id[p]) {
            p = id[p];
        }
        while (pp != id[pp]) {
            int ppp = pp;
            pp = id[pp];
            id[ppp] = p;
        }
        return p;
    }

    public static void main(String[] args) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        run(Ex1512.class);
    }
}
