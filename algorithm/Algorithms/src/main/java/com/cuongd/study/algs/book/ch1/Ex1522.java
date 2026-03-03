package com.cuongd.study.algs.book.ch1;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class Ex1522 {
    private static final int T = 32;

    public static void main(String[] args) {
        for (int i = 0; i < T; i++) {
            int N = (int) Math.pow(2, i), count;

            Stopwatch timer = new Stopwatch();
            count = Ex1517.count(N);
            StdOut.printf("N=%d\tcount=%.2f\ttime=%.4f\n",
                    N, (double) count/N, timer.elapsedTime());
        }
    }
}
