package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.In;

public class Ex5328 {
    private boolean found;

    public Ex5328(String pat, In in) {
        char[] buf = new char[pat.length()];
        int head = 0;
        boolean wrapAround = false;
        while (true) {
            char c = in.readChar();
            if (c == '\n') break;
            buf[head++] = c;
            if (head == buf.length) {
                wrapAround = true;
                head = 0;
            }
        }
        if (!wrapAround) {
            found = false;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pat.length(); i++) {
                sb.append(buf[head++]);
                if (head == buf.length) {
                    head = 0;
                }
            }
            found = sb.toString().equals(pat);
        }
    }

    public boolean found() {
        return found;
    }

    public static void main(String[] args) {
        assert new Ex5328("abc", new In()).found();
    }
}
