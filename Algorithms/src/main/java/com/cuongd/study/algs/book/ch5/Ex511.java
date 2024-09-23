package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.LinearProbingHashST;

public class Ex511 {
    public static void sort(char[] a, int R) {
        int[] count = new int[R];
        // Count.
        for (char c : a) {
            count[c]++;
        }
        // Build ST.
        LinearProbingHashST<Integer, Integer> st = new LinearProbingHashST<>();
        int sum = 0;
        for (int r = 0; r < R; r++) {
            if (count[r] != 0) {
                st.put(r, sum);
                sum += count[r];
            }
        }
        // Distribute.
        char[] aux = new char[a.length];
        for (char key : a) {
            int pos = st.get((int) key);
            aux[pos] = key;
            st.put((int) key, pos + 1);
        }
        // Copy.
        System.arraycopy(aux, 0, a, 0, aux.length);
    }

    public static void main(String[] args) {
        char[] a = new char[]{'a', 'b', 'c', 'a', 'c', 'b', 'b', 'f', 'e'};
        sort(a, 128);
        for (char c : a) {
            System.out.printf("%c ", c);
        }
    }
}
