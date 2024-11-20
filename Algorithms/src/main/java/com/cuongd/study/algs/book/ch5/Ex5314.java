package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.StdOut;

public class Ex5314 {
    private char[] pat;
    private int[][] dfa;

    public Ex5314(char[] pat) {
        this.pat = pat;
        int m = pat.length;
        int R = 256;
        dfa = new int[R][m];
        dfa[pat[0]][0] = 1;
        for (int x = 0, j = 1; j < m; j++) {
            for (int c = 0; c < R; c++) {
                dfa[c][j] = dfa[c][x];
            }
            dfa[pat[j]][j] = j+1;
            x = dfa[pat[j]][x];
        }
    }

    public int search(char[] txt) {
        int i, j, n = txt.length, m = pat.length;
        for (i = 0, j = 0; i < n && j < m; i++) {
            j = dfa[txt[i]][j];
        }
        if (j == m) return i - m;
        return n;
    }

    public static void main(String[] args) {
        char[] pat = args[0].toCharArray();
        char[] txt = args[1].toCharArray();
        Ex5314 kmp = new Ex5314(pat);
        StdOut.print("text:    ");
        for (char c : txt) StdOut.print(c);
        StdOut.println();
        int offset = kmp.search(txt);
        StdOut.print("pattern: ");
        for (int i = 0; i < offset; i++) {
            StdOut.print(" ");
        }
        for (char c : pat) StdOut.print(c);
        StdOut.println();
    }
}
