package com.cuongd.study.algsp1.book.ch2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

/** Doubling test */
public class Ex2131 extends ShellSort { // sort method goes here
    public static void main(String[] args) {
        double guessTime = 0;
        Ex2131 sorter = new Ex2131();

        for (int n = 1000; ; n *= 2) {
            Double[] a = new Double[n];
            double elapsedTime;

            for (int i = 0; i < n; i++)
                a[i] = StdRandom.uniform();
            Stopwatch timer = new Stopwatch();
            sorter.sort(a);
            elapsedTime = timer.elapsedTime();
            if (n <= 32000) // empirical
                guessTime = elapsedTime;
            else
                guessTime *= 4; // O(n^2)
            StdOut.printf("%d\t\t%.2f\t\t%.2f\n", n, guessTime, elapsedTime);
        }
    }
}
