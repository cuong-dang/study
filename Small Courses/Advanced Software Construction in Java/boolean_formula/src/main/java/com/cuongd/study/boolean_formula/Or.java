package com.cuongd.study.boolean_formula;

public class Or extends BinaryOp {
    public Or(Formula left, Formula right) {
        super(left, right);
    }

    @Override
    public boolean evaluate(Environment env) {
        return left.evaluate(env) || right.evaluate(env);
    }
}
