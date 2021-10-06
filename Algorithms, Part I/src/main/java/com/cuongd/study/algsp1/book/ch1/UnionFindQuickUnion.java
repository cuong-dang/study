package com.cuongd.study.algsp1.book.ch1;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class UnionFindQuickUnion {
    private static final String IN_PATH = "./src/main/resources/ch1/largeUF.txt";

    private final int[] id;
    private int count;

    public UnionFindQuickUnion(int N) {
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

    public int find(int p) {
        while (id[p] != p) {
            p = id[p];
        }
        return p;
    }

    public void union(int p, int q) {
        int i = find(p);
        int j = find(q);

        if (i == j) {
            return;
        }
        id[i] = j;
        count--;
    }

    public static void main(String[] args) {
        In in = new In(IN_PATH);
        int N = in.readInt();
        UnionFindQuickUnion uf = new UnionFindQuickUnion(N);
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
    }
}
