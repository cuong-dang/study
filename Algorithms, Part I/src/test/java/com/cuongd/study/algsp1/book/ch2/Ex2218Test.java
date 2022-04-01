package com.cuongd.study.algsp1.book.ch2;

import org.junit.Test;

import static com.cuongd.study.algsp1.book.ch2.Ex2219.countInversions;
import static org.junit.Assert.assertEquals;

public class Ex2218Test {
    @Test
    public void testCountInversionsSimple() {
        Integer[] a = {5, 3, 7, 4};

        assertEquals(3, countInversions(a));
    }

    @Test
    public void testCountInversionsModerate() {
        Integer[] a ={3, 4, 2, 6, 5, 0, 1};

        assertEquals(13, countInversions(a));
    }

    @Test
    public void testCountInversionsComplex() {
        Integer[] a ={2, 5, 7, 6, 4, 3, 1, 8, 9, 0};

        assertEquals(23, countInversions(a));
    }
}
