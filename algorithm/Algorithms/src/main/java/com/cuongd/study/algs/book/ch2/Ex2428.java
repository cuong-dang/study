package com.cuongd.study.algs.book.ch2;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class Ex2428 {
    private static final int N = 10_00000000;
    private static final int M = 10_0000;

    public static void main(String[] args) {
        MaxPQ<Double> pq = new MaxPQ<>();
        Instant start = Instant.now();
        Random rand = new Random();
        for (int i = 0; i < N; ++i) {
            double x = rand.nextDouble(), y = rand.nextDouble(), z = rand.nextDouble();
            pq.insert(dist(x, y, z));
            if (pq.size() > M)
                pq.delMax();
        }
        Instant end = Instant.now();
        System.out.printf("Time elapsed: %d\n", Duration.between(start, end).toSeconds());
    }

    private static double dist(double x, double y, double z) {
        return Math.sqrt(x*x + y*y + z*z);
    }
}
