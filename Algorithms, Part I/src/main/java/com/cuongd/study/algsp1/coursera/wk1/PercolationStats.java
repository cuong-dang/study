package com.cuongd.study.algsp1.coursera.wk1;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final double[] thresholds;
    private final int T;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }

        T = trials;
        thresholds = new double[trials];

        for (int i = 0; i < trials; i++) {
            Percolation exp = new Percolation(n);

            while (!exp.percolates()) {
                int randRow = StdRandom.uniform(n) + 1;
                int randCol = StdRandom.uniform(n) + 1;

                exp.open(randRow, randCol);
            }
            thresholds[i] = (double) exp.numberOfOpenSites() / n;
        }
    }

    public double mean() {
        return StdStats.mean(thresholds);
    }

    public double stddev() {
        return StdStats.stddev(thresholds);
    }

    public double confidenceLo() {
        return mean() - 1.96*stddev()/Math.sqrt(T);
    }

    public double confidenceHi() {
        return mean() + 1.96*stddev()/Math.sqrt(T);
    }

    public static void main(String[] args) {
        PercolationStats ps = new PercolationStats(StdIn.readInt(), StdIn.readInt());

        StdOut.println("mean\t\t\t= " + ps.mean());
        StdOut.println("stddev\t\t\t= " + ps.mean());
        StdOut.println(String.format("95%% confidence interval\t= [%f, %f]",
                ps.confidenceLo(), ps.confidenceHi()));
    }
}
