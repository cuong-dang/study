package com.cuongd.study.dragon.ch2.lexer;

public class Token {
    public final int tag;

    public Token(int t) {
        tag = t;
    }

    @Override
    public String toString() {
        return String.valueOf((char)tag);
    }
}
