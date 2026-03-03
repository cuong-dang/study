package com.cuongd.study.boolean_formula;

import java.util.HashSet;
import java.util.Set;

abstract class BinaryOp implements Formula {
    protected final Formula left;
    protected final Formula right;

    protected BinaryOp(Formula left, Formula right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Set<Variable> variables() {
        Set<Variable> leftVariables = left.variables(), rightVariables = right.variables(), set = new HashSet<>();
        set.addAll(leftVariables);
        set.addAll(rightVariables);
        return set;
    }
}
