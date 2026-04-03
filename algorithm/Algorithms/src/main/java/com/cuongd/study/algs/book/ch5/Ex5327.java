package com.cuongd.study.algs.book.ch5;


import java.util.List;

public class Ex5327 {
    private static int tandem(String s, String pat) {
        KMP kmp = new KMP(pat);
        List<Integer> all = kmp.findAll(s);
        int result = s.length(), longest = 0, start = 0, curr = 0, prev = 0;
        for (int i : all) {
            if (curr == 0) {
                start = i;
                curr = 1;
                result = i;
            } else if (i == prev + pat.length()) {
                curr++;
            } else {
                if (curr > longest) {
                   longest = curr;
                   result = start;
                }
                start = i;
                curr = 1;
            }
            prev = i;
        }
        if (curr > longest) {
            result = start;
        }
        return result;
    }

    public static void main(String[] args) {
        assert tandem("ab", "ba") == 2;
        assert tandem("ab", "ab") == 0;
        assert tandem("abab", "ab") == 0;
        assert tandem("abbabab", "ab") == 3;
        assert tandem("babbababbbababab", "ab") == 10;
        assert tandem("abcabcababcababcababcab", "abcab") == 3;
    }
}
