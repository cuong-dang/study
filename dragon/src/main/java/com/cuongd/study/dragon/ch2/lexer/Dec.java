package com.cuongd.study.dragon.ch2.lexer;

public class Dec extends Token {
    public final double value;

    public Dec(double v) {
        super(Tag.DEC);
        value = v;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
