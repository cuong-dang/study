package com.cuongd.study.dragon.ch2;

import com.cuongd.study.dragon.util.Matcher;

public class Ex246b {
    public static void parse(String s) {
        Matcher m = new Matcher(s);
        root(m);
        if (m.hasNext())
            throw new RuntimeException("syntax error: unexpected input: " + m.getChar());
    }

    private static void root(Matcher m) {
        if (!m.hasNext()) return;
        if (m.getChar() == '(') {
            m.match("(");
            root(m);
            m.match(")");
            root(m);
            return;
        } else if (m.getChar() == ')') {
            return;
        }
        throw new RuntimeException("syntax error: expect (; found: " + m.getChar());
    }

    public static void main(String[] args) {
        parse("");
        parse("(())");
        parse("()()");
        parse("(())()");
        parse("()(())");
    }
}
