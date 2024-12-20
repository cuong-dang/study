package com.cuongd.study.dragon.util;

public class Matcher {
    private String s;
    private int i;

    public Matcher(String s) {
        this.s = s;
    }

    public boolean hasNext() {
        return i < s.length();
    }

    public char getChar() {
        if (!hasNext()) throw new RuntimeException("end of string");
        return s.charAt(i);
    }

    public boolean match(String t) {
        if (s.indexOf(t, i) != i) return false;
        i += t.length();
        return true;
    }
}
