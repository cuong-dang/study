package com.cuongd.study.algs.coursera.wk9;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray a = new CircularSuffixArray(s);
        int first = 0;
        char[] t = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            int j = a.index(i);
            if (j == 0) {
                first = i;
                j = s.length() - 1;
            } else {
                j--;
            }
            t[i] = s.charAt(j);
        }
        BinaryStdOut.write(first);
        for (char c : t) {
            BinaryStdOut.write(c);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();

        // build next
        int n = s.length();
        int R = 256;   // extend ASCII alphabet size
        char[] aux = new char[n];

        // compute frequency counts
        int[] count = new int[R + 1];
        for (int i = 0; i < n; i++)
            count[s.charAt(i) + 1]++;

        // compute cumulates
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];

        // move data
        int[] next = new int[n];
        for (int i = 0; i < n; i++) {
            int j = count[s.charAt(i)]++;
            next[j] = i;
            aux[j] = s.charAt(i);
        }

        // copy back
        char[] sorted = new char[n];
        for (int i = 0; i < n; i++)
            sorted[i] = aux[i];

        // reconstruct
        int i = first;
        for (int j = 0; j < s.length(); j++) {
            BinaryStdOut.write(sorted[i]);
            i = next[i];
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        if (args[0].equals("+")) inverseTransform();
    }
}
