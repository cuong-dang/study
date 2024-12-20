package com.cuongd.study.dragon.ch2.lexer;

public class CmpOp extends Token {
    public static final int LT = 0, LTEQ = 1, EQ = 2, NEQ = 3, GTEQ = 4, GT = 5;
    public final int op;

    public CmpOp(int o) {
        super(Tag.CMP_OP);
        op = o;
    }

    @Override
    public String toString() {
        switch (op) {
            case 0:
                return "<";
            case 1:
                return "<=";
            case 2:
                return "==";
            case 3:
                return "!=";
            case 4:
                return ">=";
            case 5:
                return ">";
        }
        throw new RuntimeException("Unexpected CmpOp op: " + op);
    }
}
