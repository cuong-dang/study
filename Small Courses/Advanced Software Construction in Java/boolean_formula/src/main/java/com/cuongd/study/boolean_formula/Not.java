package com.cuongd.study.boolean_formula;

import java.util.Set;

public class Not implements Formula {
    private final Formula f;

    public Not(Formula f) {
        this.f = f;
    }

    @Override
    public boolean evaluate(Environment env) {
        return !f.evaluate(env);
    }

    @Override
    public Set<Variable> variables() {
        return f.variables();
    }
}
