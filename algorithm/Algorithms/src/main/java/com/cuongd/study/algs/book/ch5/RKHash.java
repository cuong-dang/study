package com.cuongd.study.algs.book.ch5;

import java.math.BigInteger;
import java.util.Random;

public class RKHash {
    private long h;
    private long q;
    private int R;
    private long RM;
    private boolean skipRmOnce;

    public RKHash() {
        q = longRandomPrime();
        R = 256;
        RM = 1;
        skipRmOnce = true;
    }

    public long value() {
        return h;
    }

    public void append(char c) {
        h = (h * R + c) % q;
        if (skipRmOnce) {
            skipRmOnce = false;
        } else {
            RM = (RM * R) % q;
        }
    }

    public void append(String s) {
        for (int i = 0; i < s.length(); i++) {
            append(s.charAt(i));
        }
    }

    public void slide(char a, char c) {
        h = (h + q - RM * a % q) % q;
        h = (h * R + c) % q;
    }

    public void copy(RKHash that) {
        this.h = that.h;
        this.q = that.q;
        this.R = that.R;
        this.RM = that.RM;
        this.skipRmOnce = that.skipRmOnce;
    }

    private static long longRandomPrime() {
        BigInteger prime = BigInteger.probablePrime(31, new Random());
        return prime.longValue();
    }

    public static void main(String[] args) {
        RKHash actual = new RKHash();
        actual.append('a');
        actual.append('b');
        RKHash expected = new RKHash();
        expected.append('b');
        expected.append('c');
        actual.slide('a', 'c');
        assert actual.value() == expected.value();
    }
}
