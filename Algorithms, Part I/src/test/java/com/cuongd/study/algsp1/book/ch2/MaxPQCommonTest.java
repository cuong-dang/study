package com.cuongd.study.algsp1.book.ch2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MaxPQCommonTest<T extends MaxPQ<Integer>> {
    @Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                { Ex2426.class }
        });
    }

    private final Class<T> maxPQClass;
    private T pq;

    public MaxPQCommonTest(Class<T> maxPQClass) {
        this.maxPQClass = maxPQClass;
    }

    @Test
    public void testBasic() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        pq = maxPQClass.getDeclaredConstructor().newInstance();
        pq.insert(1);
        assertEquals(1, (int) pq.delMax());
        pq.insert(2);
        pq.insert(3);
        assertEquals(3, (int) pq.delMax());
        assertEquals(2, (int) pq.delMax());
    }

    @Test
    public void testCommon() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        pq = maxPQClass.getDeclaredConstructor().newInstance();
        pq.insert(4);
        pq.insert(3);
        pq.insert(6);
        pq.insert(7);
        pq.insert(5);
        assertEquals(7, (int) pq.delMax());
        assertEquals(6, (int) pq.delMax());
        assertEquals(5, (int) pq.delMax());
        pq.insert(2);
        pq.insert(9);
        assertEquals(9, (int) pq.delMax());
        assertEquals(4, (int) pq.delMax());
        pq.insert(0);
        pq.insert(1);
        pq.insert(8);
        assertEquals(8, (int) pq.delMax());
        assertEquals(3, (int) pq.delMax());
        assertEquals(2, (int) pq.delMax());
        assertEquals(1, (int) pq.delMax());
        assertEquals(0, (int) pq.delMax());
    }
}
