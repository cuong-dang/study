package com.cuongd.study.algsp1.coursera.wk1;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final double[] thresholds;
    private final int t;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }

        t = trials;
        thresholds = new double[trials];

        for (int i = 0; i < trials; i++) {
            Percolation exp = new Percolation(n);

            while (!exp.percolates()) {
                int randRow = StdRandom.uniform(n) + 1;
                int randCol = StdRandom.uniform(n) + 1;

                exp.open(randRow, randCol);
            }
            thresholds[i] = (double) exp.numberOfOpenSites() / (n*n);
        }

    }

    public double mean() {
        return StdStats.mean(thresholds);
    }

    public double stddev() {
        return StdStats.stddevp(thresholds);
    }

    public double confidenceLo() {
        return mean() - 1.96*stddev()/Math.sqrt(t);
    }

    public double confidenceHi() {
        return mean() + 1.96*stddev()/Math.sqrt(t);
    }

    public static void main(String[] args) {
        PercolationStats ps = new PercolationStats(Integer.parseInt(args[0]),
                Integer.parseInt(args[1]));

        StdOut.println("mean\t\t\t\t\t= " + ps.mean());
        StdOut.println("stddev\t\t\t\t\t= " + ps.stddev());
        StdOut.println(String.format("95%% confidence interval\t= [%f, %f]",
                ps.confidenceLo(), ps.confidenceHi()));
    }
}
