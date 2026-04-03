package com.cuongd.study.boolean_formula;

import java.util.*;

public interface Environment {
    static Environment defaultEnvironment() {
        return new ImListEnvironment();
    }

    static Collection<Environment> generateAllEnvironments(Set<Variable> vars) {
        List<Environment> result = new ArrayList<>();
        if (vars.isEmpty()) {
            result.add(Environment.defaultEnvironment());
            return result;
        }
        Variable first = (Variable) vars.toArray()[0];
        Set<Variable> t = new HashSet<>(), tt = new HashSet<>(vars);
        t.add(first);
        tt.removeAll(t);
        Collection<Environment> partialEnvs = generateAllEnvironments(tt);
        for (Environment partialEnv : partialEnvs) {
            result.add(partialEnv.install(first, true));
            result.add(partialEnv.install(first, false));
        }
        return result;
    }

    boolean lookUp(Variable x);
    Environment install(Variable x, boolean v);
}
