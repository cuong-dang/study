package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex4134Test {
    @Test
    public void test() {
        SymbolGraphOnePass sg = new SymbolGraphOnePass("src/test/resources/ch4/ex4134.txt", " ");
        assertEquals(4, sg.V());
        assertEquals(4, sg.E());

        List<String> actual = new ArrayList<>(), expected = new ArrayList<>();
        sg.adj("a").forEach(actual::add);
        expected.add("c");
        expected.add("b");
        assertEquals(expected, actual);

        actual.clear();
        sg.adj("b").forEach(actual::add);
        expected.clear();
        expected.add("c");
        expected.add("a");
        assertEquals(expected, actual);

        actual.clear();
        sg.adj("c").forEach(actual::add);
        expected.clear();
        expected.add("d");
        expected.add("b");
        expected.add("a");
        assertEquals(expected, actual);

        actual.clear();
        sg.adj("d").forEach(actual::add);
        expected.clear();
        expected.add("c");
        assertEquals(expected, actual);
    }
}
