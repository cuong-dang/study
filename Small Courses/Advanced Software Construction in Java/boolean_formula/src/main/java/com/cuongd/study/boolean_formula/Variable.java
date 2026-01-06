package com.cuongd.study.boolean_formula;

import java.util.HashSet;
import java.util.Set;

public class Variable implements Formula {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    @Override
    public boolean evaluate(Environment env) {
        return env.lookUp(this);
    }

    @Override
    public Set<Variable> variables() {
        Set<Variable> set = new HashSet<>();
        set.add(this);
        return set;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Variable that = (Variable) obj;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
