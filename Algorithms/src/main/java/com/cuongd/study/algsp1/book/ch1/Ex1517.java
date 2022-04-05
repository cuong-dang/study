package com.cuongd.study.algsp1.book.ch1;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Ex1517 {
    private static final int N = 4096;

    public static int count(int N) {
        int result = 0;

        UnionFind uf = new UnionFindWeightedQuickUnion(N);
        while (uf.count() != 1) {
            int p = StdRandom.uniform(N), q = StdRandom.uniform(N);

            result++;
            if (p == q || uf.connected(p, q)) continue;
            uf.union(p, q);
        }
        return result;
    }

    public static void main(String[] args) {
        StdOut.print(count(N));
    }
}
