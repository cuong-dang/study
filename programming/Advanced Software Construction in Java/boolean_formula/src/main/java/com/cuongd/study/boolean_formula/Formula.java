package com.cuongd.study.boolean_formula;

import java.util.Collection;
import java.util.Set;

public interface Formula {
    boolean evaluate(Environment env);
    default Environment isSatisfiable() {
        Collection<Environment> allEnvs = Environment.generateAllEnvironments(variables());

        for (Environment env : allEnvs) {
            if (evaluate(env)) {
                return env;
            }
        }
        return Environment.defaultEnvironment();
    }

    Set<Variable> variables();
}
