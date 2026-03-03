package com.cuongd.study.boolean_formula;

import java.util.Set;

public class And extends BinaryOp {
    public And(Formula left, Formula right) {
        super(left, right);
    }

    @Override
    public boolean evaluate(Environment env) {
        return left.evaluate(env) && right.evaluate(env);
    }
}
