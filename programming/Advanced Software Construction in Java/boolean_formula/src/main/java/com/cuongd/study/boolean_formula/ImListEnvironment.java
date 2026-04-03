package com.cuongd.study.boolean_formula;

import com.cuongd.study.imlist.ImList;
import com.cuongd.study.imlist.Pair;

public class ImListEnvironment implements Environment {
    private final ImList<Pair<Variable, Boolean>> env;

    public ImListEnvironment() {
        env = ImList.empty();
    }

    public ImListEnvironment(ImList<Pair<Variable, Boolean>> env) {
        this.env = env;
    }

    @Override
    public boolean lookUp(Variable x) {
        return lookUp(env, x);
    }

    private boolean lookUp(ImList<Pair<Variable, Boolean>> env, Variable x) {
        if (env.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (env.first().first().equals(x)) {
            return env.first().second();
        }
        return lookUp(env.rest(), x);
    }

    @Override
    public Environment install(Variable x, boolean v) {
        return new ImListEnvironment(env.cons(new Pair<>(x, v)));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ImList<Pair<Variable, Boolean>> t = env;
        while (!t.isEmpty()) {
            sb.append(t.first().first().name()).append("=").append(t.first().second().toString()).append(";");
            t = t.rest();
        }
        return sb.toString();
    }
}
