package com.cuongd.study.algs.book.ch5;

import java.util.ArrayList;
import java.util.List;

public class KMP {
    private String pat;
    private int[][] dfa;

    public KMP(String pat) {
        this.pat = pat;
        int m = pat.length();
        int R = 256;
        dfa = new int[R][m];
        dfa[pat.charAt(0)][0] = 1;
        for (int x = 0, j = 1; j < m; j++) {
            for (int c = 0; c < R; c++) {
                dfa[c][j] = dfa[c][x];
            }
            dfa[pat.charAt(j)][j] = j+1;
            x = dfa[pat.charAt(j)][x];
        }
    }

    public int search(String txt) {
        return search(txt, 0);
    }

    public List<Integer> findAll(String txt) {
        List<Integer> result = new ArrayList<>();
        int from = 0, i;
        while ((i = search(txt, from)) != txt.length()) {
            result.add(i);
            from = i+1;
        }
        return result;
    }

    private int search(String txt, int from) {
        int i, j, n = txt.length(), m = pat.length();
        for (i = 0, j = 0; i < n && j < m; i++) {
            j = dfa[txt.charAt(i)][j];
        }
        if (j == m) return i - m;
        return n;
    }

    public static void main(String[] args) {
        KMP kmp = new KMP("ABA");
        assert List.of(0, 7).equals(kmp.findAll("ABAABBAABA"));
    }
}
