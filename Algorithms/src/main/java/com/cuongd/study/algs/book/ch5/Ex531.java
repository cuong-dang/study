package com.cuongd.study.algs.book.ch5;

import java.util.ArrayList;
import java.util.List;

public class Ex531 {
    private String pat;

    public Ex531(String pat) {
        this.pat = pat;
    }

    public int search(String txt) {
        return searchFrom(txt, 0);
    }

    public int count(String txt) {
        return searchAll(txt).size();
    }

    public List<Integer> searchAll(String txt) {
        List<Integer> result = new ArrayList<>();
        int from = 0;
        while (from <= txt.length() - pat.length()) {
            int i = searchFrom(txt, from);
            if (i != txt.length()) {
                result.add(i);
                from = i + 1;
            } else {
                return result;
            }
        }
        return result;
    }

    private int searchFrom(String txt, int from) {
        int i;

        for (i = from; i <= txt.length() - pat.length(); i++) {
            int j = 0;
            for (int k = i; j < pat.length(); j++, k++) {
                if (txt.charAt(k) != pat.charAt(j)) {
                    break;
                }
            }
            if (j == pat.length()) return i;
        }
        return txt.length();
    }

    public static void main(String[] args) {
        Ex531 s = new Ex531("AACAA");
        assert s.search("AACAA") == 0;
        assert s.search("AABRAACADABRAACAADABRA") == 12;
        assert s.search("ABCDEFG") == 7;

        assert s.count("AACAAAACAA") == 2;
        assert s.searchAll("AACAAAACAA").equals(List.of(0, 5));
        assert s.count("AACAACAA") == 2;
        assert s.searchAll("AACAACAA").equals(List.of(0, 3));
    }
}
