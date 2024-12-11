package com.cuongd.study.dragon.ch2.lexer;

public class Num extends Token {
    public final int value;

    public Num(int v) {
        super(Tag.NUM);
        value = v;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
