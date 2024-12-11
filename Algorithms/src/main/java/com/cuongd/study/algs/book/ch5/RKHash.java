package com.cuongd.study.algs.book.ch5;

import java.math.BigInteger;
import java.util.Random;

public class RKHash {
    private long h;
    private long q;
    private int R;

    public RKHash() {
        q = longRandomPrime();
        R = 256;
    }

    public long hash() {
        return h;
    }

    public void append(char c) {
        h = (h * R + c) % q;
    }

    public void append(String s) {
        for (int i = 0; i < s.length(); i++) {
            h = (h * R + s.charAt(i)) % q;
        }
    }

    public void rm(int i) {
        
    }

    private static long longRandomPrime() {
        BigInteger prime = BigInteger.probablePrime(31, new Random());
        return prime.longValue();
    }
}
