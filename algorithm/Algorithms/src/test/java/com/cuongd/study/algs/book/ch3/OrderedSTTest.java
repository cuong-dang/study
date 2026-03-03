package com.cuongd.study.algs.book.ch3;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OrderedSTTest {
    private final Class<OrderedST<String, Integer>> stClass;
    private OrderedST<String, Integer> st;
    private List<String> keys;

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                { Ex313.class },
                { BST.class },
                { Ex3213.class },
                { Ex3234.class },
        });
    }

    public OrderedSTTest(Class<OrderedST<String, Integer>> stClass) {
        this.stClass = stClass;
    }

    @Before
    public void init() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        st = stClass.getDeclaredConstructor().newInstance();
        keys = new ArrayList<>();
    }

    @Test
    public void testPutEmpty() {
        st.put("S", 0);
        assertEquals((Integer) 0, st.get("S"));
        assertEquals(1, st.size());
        st.keys().forEach(keys::add);
        assertEquals(List.of("S"), keys);
    }

    @Test
    public void testPutHead() {
        st.put("S", 0);
        st.put("E", 1);
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("E", "S"), keys);
    }

    @Test
    public void testPutLast() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("E", "S", "X"), keys);
    }

    @Test
    public void testPutMid() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("E", "R", "S", "X"), keys);
    }

    @Test
    public void testPutReplace() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        assertEquals((Integer) 1, st.get("E"));
        assertEquals(4, st.size());
        st.put("E", 6);
        assertEquals((Integer) 6, st.get("E"));
        assertEquals(4, st.size());
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("E", "R", "S", "X"), keys);
    }

    @Test
    public void testGet() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        assertEquals((Integer) 0, st.get("S"));
        assertEquals((Integer) 1, st.get("E"));
        assertEquals((Integer) 7, st.get("X"));
        assertEquals((Integer) 3, st.get("R"));
        assertNull(st.get("A"));
    }

    @Test
    public void testDeleteMin() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        st.deleteMin();
        assertEquals(3, st.size());
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("R", "S", "X"), keys);
    }

    @Test
    public void testDeleteMax() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        st.deleteMax();
        assertEquals(3, st.size());
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("E", "R", "S"), keys);
    }

    @Test
    public void testDeleteOne() {
        st.put("S", 0);;
        st.delete("S");
        assertEquals(0, st.size());
    }

    @Test
    public void testDeleteHead() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        st.delete("E");
        assertEquals(3, st.size());
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("R", "S", "X"), keys);
    }

    @Test
    public void testDeleteLast() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        st.delete("X");
        assertEquals(3, st.size());
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("E", "R", "S"), keys);
    }

    @Test
    public void testDeleteMid() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        st.delete("R");
        assertEquals(3, st.size());
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("E", "S", "X"), keys);
    }

    @Test
    public void testDeleteMid2() {
        st.put("S", 0); st.put("E", 1); st.put("A", 2); st.put("R", 3); st.put("C", 4);
        st.put("H", 5); st.put("E", 6); st.put("X", 7); st.put("A", 8); st.put("M", 9);
        st.put("P", 10); st.put("L", 11); st.put("E", 12);
        st.delete("E");
        assertEquals(9, st.size());
        st.keys().forEach(keys::add);
        assertEquals(Arrays.asList("A", "C", "H", "L", "M", "P", "R", "S", "X"), keys);
    }

    @Test
    public void testContains() {
        st.put("S", 0);
        assertTrue(st.contains("S"));
        assertFalse(st.contains("A"));
    }

    @Test
    public void testMinMax() {
        assertNull(st.min());
        assertNull(st.max());
        st.put("S", 0);
        assertEquals("S", st.min());
        assertEquals("S", st.max());
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        assertEquals("E", st.min());
        assertEquals("X", st.max());
    }

    @Test
    public void testFloor() {
        assertNull(st.floor("S"));
        st.put("S", 0);
        assertNull(st.floor("A"));
        assertEquals("S", st.floor("S"));
        assertEquals("S", st.floor("X"));
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        assertEquals("S", st.floor("T"));
    }

    @Test
    public void testFloor2() {
        st.put("S", 0); st.put("E", 1); st.put("A", 2); st.put("R", 3); st.put("C", 4);
        st.put("H", 5); st.put("E", 6); st.put("X", 7); st.put("A", 8); st.put("M", 9);
        st.put("P", 10); st.put("L", 11); st.put("E", 12);
        assertEquals("H", st.floor("H"));
        assertEquals("H", st.floor("I"));
    }

    @Test
    public void testCeiling() {
        assertNull(st.ceiling("S"));
        st.put("S", 0);
        assertNull(st.ceiling("T"));
        assertEquals("S", st.ceiling("S"));
        assertEquals("S", st.ceiling("A"));
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        assertEquals("R", st.ceiling("Q"));
    }

    @Test
    public void testRank() {
        assertEquals(0, st.rank("A"));
        st.put("S", 0);
        assertEquals(0, st.rank("A"));
        assertEquals(0, st.rank("S"));
        assertEquals(1, st.rank("T"));
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        assertEquals(3, st.rank("X"));
    }

    @Test
    public void testSelect() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        assertEquals("E", st.select(0));
        assertEquals("R", st.select(1));
        assertEquals("S", st.select(2));
        assertEquals("X", st.select(3));
    }

    @Test
    public void testSize() {
        st.put("S", 0);
        st.put("E", 1);
        st.put("X", 7);
        st.put("R", 3);
        assertEquals(1, st.size("E", "E"));
        assertEquals(3, st.size("E", "S"));
        assertEquals(3, st.size("E", "T"));
        assertEquals(1, st.size("Q", "R"));
        assertEquals(2, st.size("Q", "S"));
        assertEquals(2, st.size("Q", "T"));
    }
}
