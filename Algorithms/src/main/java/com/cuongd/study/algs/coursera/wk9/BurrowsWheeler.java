package com.cuongd.study.algs.coursera.wk9;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.LSD;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;

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
        ST<Character, Queue<Integer>> st = new ST<>();
        String[] sorted = new String[s.length()];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!st.contains(c)) {
                st.put(c, new Queue<>());
            }
            st.get(c).enqueue(i);
            sorted[i] = "" + c;
        }
        LSD.sort(sorted, 1);
        int[] next = new int[sorted.length];
        for (int i = 0; i < sorted.length; i++) {
            char c = sorted[i].charAt(0);
            next[i] = st.get(c).dequeue();
        }
        // reconstruct
        int i = first;
        do {
            BinaryStdOut.write(sorted[i]);
            i = next[i];
        } while (i != first);
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        if (args[0].equals("+")) inverseTransform();
    }
}
