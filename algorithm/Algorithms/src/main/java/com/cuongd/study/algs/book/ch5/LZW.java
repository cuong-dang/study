package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.TST;

public class LZW {
    private static final int R = 256; // number of input chars
    private static final int L = 4096; // number of codewords = 2^12
    private static final int WIDTH = 12;

    public static void compress() {
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<>();

        for (int i = 0; i < R; i++) {
            st.put("" + (char) i, i);
        }
        int code = R + 1;

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);
            BinaryStdOut.write(st.get(s), WIDTH);
            int t = s.length();
            if (t < input.length() && code < L)
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);
        }
        BinaryStdOut.write(R, WIDTH);
        BinaryStdOut.close();
    }

    public static void expand() {
        String[] st = new String[L];
        int i; // next available codeword value
        for (i = 0; i < R; i++) { // Initialize table for chars.
            st[i] = "" + (char) i;
        }
        st[i++] = " "; // (unused) lookahead for EOF
        int codeword = BinaryStdIn.readInt(WIDTH);
        String val = st[codeword];
        while (true) {
            BinaryStdOut.write(val); // Write current substring.
            codeword = BinaryStdIn.readInt(WIDTH);
            if (codeword == R) break;
            String s = st[codeword]; // Get next codeword.
            if (i == codeword) { // If lookahead is invalid,
                s = val + val.charAt(0); // make codeword from last one.
            }
            if (i < L)
                st[i++] = val + s.charAt(0); // Add new entry to code table.
            val = s; // Update current codeword.
        }
        BinaryStdOut.close();
    }
}
