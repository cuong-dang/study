package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.math.BigInteger;
import java.util.Random;

public class Ex5323 {
    private String s;
    private int R;
    private long q;
    private long leftHash;
    private long rightHash;

    public Ex5323() {
        R = 256;
        q = longRandomPrime();
        leftHash = rightHash = 0;
        s = "";
    }

    public boolean isPalindrome() {
        return leftHash == rightHash;
    }

    public String s() {
        return s;
    }

    public void addChar(char c) {
        s += c;
        if (s.length() < 2) return;
        int b = s.length()/2 - 1;
        if (s.length() % 2 == 0) { // left: add; right: add;
            int lC = s.charAt(s.length()/2 - 1);
            leftHash = ((leftHash * R) % q + lC) % q;
            rightHash = (((c * (long) Math.pow(R, b)) % q) + rightHash) % q;
        } else { // right: drop + add;
            // drop
            char mC = s.charAt(s.length()/2);
            rightHash = ((rightHash - mC % q) % q);
            rightHash = (rightHash % (R*q)) / R;
            // add
            rightHash = (((c * (long) Math.pow(R, b)) % q) + rightHash) % q;
        }
    }

    private static long longRandomPrime() {
        BigInteger prime = BigInteger.probablePrime(31, new Random());
        return prime.longValue();
    }

    public static void main(String[] args) {
        Ex5323 p = new Ex5323();
        while (true) {
            StdOut.print("> ");
            p.addChar(StdIn.readLine().charAt(0));
            StdOut.print(p.s() + ": ");
            if (p.isPalindrome()) {
                StdOut.println("palindrome");
            } else {
                StdOut.println("not palindrome");
            }
        }
    }
}
