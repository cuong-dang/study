package com.cuongd.study.dragon.ch2.lexer;

public class Word extends Token {
    public final String lexeme;

    public Word(int t, String s) {
        super(t);
        lexeme = new String(s);
    }
}
