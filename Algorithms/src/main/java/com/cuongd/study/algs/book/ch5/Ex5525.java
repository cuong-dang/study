package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class Ex5525 {
    private static final String ALPHABET = "abc";

    private static void compress() {
        BinaryStdOut.write(ALPHABET.length());
        BinaryStdOut.write(ALPHABET);
        char inChar = '\0', alphabetChar;
        boolean leftOver = true;
        inChar = BinaryStdIn.readChar();
        while (!BinaryStdIn.isEmpty()) {
            for (int i = 0; i < ALPHABET.length(); i++) {
                alphabetChar = ALPHABET.charAt(i);
                int count = 0;
                while (inChar == alphabetChar) {
                    count++;
                    if (!BinaryStdIn.isEmpty()) {
                        inChar = BinaryStdIn.readChar();
                    } else {
                        leftOver = false;
                        break;
                    }
                }
                BinaryStdOut.write(count, 8);
            }
        }
        if (leftOver) {
            for (int i = 0; i < ALPHABET.length(); i++) {
                alphabetChar = ALPHABET.charAt(i);
                if (inChar == alphabetChar) {
                    BinaryStdOut.write(1, 8);
                } else {
                    BinaryStdOut.write(0, 8);
                }
            }
        }
        BinaryStdOut.close();
    }

    private static void expand() {
        int alphabetN = BinaryStdIn.readInt();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < alphabetN; i++) {
            sb.append(BinaryStdIn.readChar());
        }
        String alphabet = sb.toString();
        while (!BinaryStdIn.isEmpty()) {
            for (int i = 0; i < alphabetN; i++) {
                char c = alphabet.charAt(i);
                int n = BinaryStdIn.readInt(8);
                for (int j = 0; j < n; j++) {
                    BinaryStdOut.write(c);
                }
            }
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if (args[0].equals("-")) compress();
        if (args[0].equals("+")) expand();
    }
}
