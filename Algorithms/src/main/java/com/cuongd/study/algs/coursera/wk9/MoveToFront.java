package com.cuongd.study.algs.coursera.wk9;

import com.cuongd.study.algs.book.ch1.MyLinkedList;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;
    private static final MyLinkedList<Character> a;

    static {
        a = new MyLinkedList<>();
        for (int i = R - 1; i >= 0; i--) {
            a.add((char) i);
        }
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int i = a.rank(c);
            if (i != 0) {
                a.delete(i);
                a.add(c);
            }
            BinaryStdOut.write(i, 8);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        while (!BinaryStdIn.isEmpty()) {
            int i = BinaryStdIn.readInt(8);
            char c = a.elemAt(i);
            if (i != 0) {
                a.delete(i);
                a.add(c);
            }
            BinaryStdOut.write(c);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        if (args[0].equals("+")) decode();
    }

}