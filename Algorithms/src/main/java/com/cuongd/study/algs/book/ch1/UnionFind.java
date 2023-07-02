package com.cuongd.study.algs.book.ch1;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.lang.reflect.InvocationTargetException;

public abstract class UnionFind {
    protected static final String IN_PATH = "./src/main/resources/ch1/largeUF.txt";

    protected final int[] id;
    protected int count;

    public UnionFind(int N) {
        count = N;
        id = new int[N];
        for (int i = 0; i < N; i++) {
            id[i] = i;
        }
    }

    public int count() {
        return count;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    abstract public int find(int p);

    abstract public void union(int p, int q);

    protected static <T extends UnionFind> void run(Class<T> ufc) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        In in = new In(IN_PATH);
        int N = in.readInt();
        UnionFind uf = ufc.getDeclaredConstructor(int.class).newInstance(N);
        while (!in.isEmpty()) {
            int p = in.readInt();
            int q = in.readInt();
            if (uf.connected(p, q)) {
                continue;
            }
            uf.union(p, q);
            StdOut.println(p + " " + q);
        }
        StdOut.println(uf.count() + " components");
        in.close();
    }
}
