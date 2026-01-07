package com.cuongd.study.boolean_formula;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class BooleanFormulaTest {
    private Environment env;

    @Before
    public void init() {
        env = new ImListEnvironment();
    }

    @Test
    public void testEvaluateOneVariable() {
        Variable x = new Variable("X");
        env = env.install(x, true);
        assertTrue(x.evaluate(env));
        env = env.install(x, false);
        assertFalse(x.evaluate(env));
    }

    @Test
    public void testEvaluateTwoVariables() {
        Variable x = new Variable("X"), y = new Variable("Y");
        env = env.install(x, true);
        env = env.install(y, false);
        assertFalse(new And(x, y).evaluate(env));
        assertTrue(new Or(x, y).evaluate(env));
        assertFalse(new Not(x).evaluate(env));
        assertTrue(new Not(y).evaluate(env));
    }

    @Test
    public void testEvaluateThreeVariables() {
        Variable x = new Variable("X"), y = new Variable("Y"), z = new Variable("Z");
        env = env.install(x, true);
        env = env.install(y, false);
        env = env.install(z, true);
        assertFalse(new And(new And(x, y), z).evaluate(env));
        assertTrue(new Or(new And(x, y), z).evaluate(env));
    }

    @Test
    public void testGenerateAllEnvironmentsOneVariable() {
        Variable x = new Variable("X");
        Set<Variable> vars = new HashSet<>();
        vars.add(x);
        Collection<Environment> envs = Environment.generateAllEnvironments(vars);
        assertEquals(envs.stream().map(Object::toString).collect(Collectors.toList()),
                Arrays.asList("X=true;", "X=false;"));
    }

    @Test
    public void testGenerateAllEnvironmentsTwoVariables() {
        Variable x = new Variable("X");
        Variable y = new Variable("Y");
        Set<Variable> vars = new HashSet<>();
        vars.add(x);
        vars.add(y);
        Collection<Environment> envs = Environment.generateAllEnvironments(vars);
        assertEquals(envs.stream().map(Object::toString).collect(Collectors.toList()),
                Arrays.asList("X=true;Y=true;", "X=false;Y=true;", "X=true;Y=false;", "X=false;Y=false;"));
    }

    @Test
    public void testGenerateAllEnvironmentsThreeVariables() {
        Variable x = new Variable("X");
        Variable y = new Variable("Y");
        Variable z = new Variable("Z");
        Set<Variable> vars = new HashSet<>();
        vars.add(x);
        vars.add(y);
        vars.add(z);
        Collection<Environment> envs = Environment.generateAllEnvironments(vars);
        assertEquals(envs.stream().map(Object::toString).collect(Collectors.toList()),
                Arrays.asList(
                        "X=true;Y=true;Z=true;", "X=false;Y=true;Z=true;", "X=true;Y=false;Z=true;",
                        "X=false;Y=false;Z=true;", "X=true;Y=true;Z=false;", "X=false;Y=true;Z=false;",
                        "X=true;Y=false;Z=false;", "X=false;Y=false;Z=false;"));
    }

    @Test
    public void testIsSatisfiableOneVariable() {
        Variable x = new Variable("X");
        assertEquals(x.isSatisfiable().toString(), "X=true;");
        assertEquals(new Not(x).isSatisfiable().toString(), "X=false;");
        assertEquals(new And(x, new Not(x)).isSatisfiable().toString(), "");
    }

    @Test
    public void testIsSatisfiableTwoVariables() {
        Variable x = new Variable("X"), y = new Variable("Y");
        assertEquals(new And(x, y).isSatisfiable().toString(), "X=true;Y=true;");
    }

    @Test
    public void testIsSatisfiableMultiVariables() {
        Variable p = new Variable("P"), q = new Variable("Q"), r = new Variable("R");
        Formula f = new And(new Or(p, q), new And(new Not(p), new And(q, new Not(r))));
        assertEquals(f.isSatisfiable().toString(), "P=false;Q=true;R=false;");
    }
}
