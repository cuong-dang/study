package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.RabinKarp;

public class Ex5326 {
    private boolean isCyclicRotation;

    public Ex5326(String s1, String s2) {
        isCyclicRotation = false;
        if (s1.length() != s2.length()) return;
        String s = s1 + s1;
        RabinKarp rk = new RabinKarp(s2);
        isCyclicRotation = rk.search(s) != s.length();
    }

    public boolean isCyclicRotation() {
        return isCyclicRotation;
    }

    public static void main(String[] args) {
        assert new Ex5326("example", "ampleex").isCyclicRotation();
        assert !new Ex5326("example", "exampleex").isCyclicRotation();
    }
}
