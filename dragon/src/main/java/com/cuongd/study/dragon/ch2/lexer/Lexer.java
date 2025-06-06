package com.cuongd.study.dragon.ch2.lexer;

import java.io.IOException;
import java.util.Hashtable;

public class Lexer {
    public int line = 1;
    private char peek = ' ';
    private char putBack = ' ';
    private Hashtable words = new Hashtable();

    void reserve(Word t) {
        words.put(t.lexeme, t);
    }

    public Lexer() {
        reserve(new Word(Tag.TRUE, "true"));
        reserve(new Word(Tag.FALSE, "false"));
    }

    public Token scan() throws IOException {
        boolean isLineComment = false, isBlockComment = false;
        for (; ; peek = read()) {
            // end of line comment?
            if (peek == '\n') {
                line = line + 1;
                if (isLineComment)
                    isLineComment = false;
                // are we in line comment?
            } else if (isLineComment) continue;
                // end of block comment?
            else if (isBlockComment && peek == '*') {
                peek = read();
                if (peek == '/')
                    isBlockComment = false;
                else
                    putBack = peek;
                // are we in block comment?
            } else if (isBlockComment) continue;
                // not in comments
            else if (peek == '/') {
                peek = read();
                if (peek == '/')
                    isLineComment = true;
                else if (peek == '*')
                    isBlockComment = true;
                else {
                    putBack = peek;
                    peek = '/';
                    break;
                }
                // skip white spaces
            } else if (peek == ' ' || peek == '\t') continue;
            else break;
        }
        if (Character.isDigit(peek) || peek == '.') {
            int v = 0;
            while (Character.isDigit(peek)) {
                v = 10 * v + Character.digit(peek, 10);
                peek = read();
            }
            if (peek == '.') {
                peek = read();
                double d = v, m = 1.0 / 10;
                while (Character.isDigit(peek)) {
                    d += m * Character.digit(peek, 10);
                    m /= 10;
                    peek = read();
                }
                return new Dec(d);
            }
            return new Num(v);
        }
        if (Character.isLetter(peek)) {
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek);
                peek = read();
            } while (Character.isLetterOrDigit(peek));
            String s = b.toString();
            Word w = (Word) words.get(s);
            if (w != null) return w;
            w = new Word(Tag.ID, s);
            words.put(s, w);
            return w;
        }
        Token t;
        switch (peek) {
            case '<':
                t = cmpOpCheckNext(CmpOp.LTEQ, CmpOp.LT);
                break;
            case '>':
                t = cmpOpCheckNext(CmpOp.GTEQ, CmpOp.GT);
                break;
            case '=':
                t = cmpOpCheckNext(CmpOp.EQ);
                break;
            case '!':
                t = cmpOpCheckNext(CmpOp.NEQ);
                break;
            default:
                t = new Token(peek);
        }
        peek = ' ';
        return t;
    }

    private Token cmpOpCheckNext(int matched, int notMatched)
            throws IOException {
        char next = read();
        if (next == '=') {
            return new CmpOp(matched);
        }
        putBack = next;
        return new CmpOp(notMatched);
    }

    private Token cmpOpCheckNext(int matched)
            throws IOException {
        char next = read();
        if (next == '=') {
            return new CmpOp(matched);
        }
        putBack = next;
        return new Token(peek);
    }

    private char read() throws IOException {
        if (putBack != ' ') {
            char t = putBack;
            putBack = ' ';
            return t;
        }
        return (char) System.in.read();
    }

    public static void main(String[] args) throws IOException {
        Lexer lex = new Lexer();
        while (true) {
            System.out.println("Scanned token: " + lex.scan());
        }
    }
}
