package com.cuongd.study.algsp1.book.ch1;

import edu.princeton.cs.algs4.Interval1D;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.cuongd.study.algsp1.book.ch1.Exercise122.findIntersects;
import static org.junit.Assert.assertEquals;

public class Exercise122Test {
    private static final Interval1D I1 = new Interval1D(0, 1);
    private static final Interval1D I2 = new Interval1D(2, 3);
    private static final Interval1D I3 = new Interval1D(1, 1.5);
    private static final Interval1D I4 = new Interval1D(0.5, 1.5);

    @Test
    public void testFindIntersectsNoIntersects() {
        assertEquals(new HashSet<ImmutablePair<Interval1D, Interval1D>>(),
                findIntersects(Arrays.asList(I1, I2)));
    }

    @Test
    public void testFindIntersectsEdgeIntersect() {
        Set<ImmutablePair<Interval1D, Interval1D>> expected =
                new HashSet<>();
        expected.add(new ImmutablePair<>(I3, I1));

        assertEquals(expected, findIntersects(Arrays.asList(I1, I3)));
    }

    @Test
    public void testFindIntersectsCommonCase() {
        Set<ImmutablePair<Interval1D, Interval1D>> expected =
                new HashSet<>();
        expected.add(new ImmutablePair<>(I3, I1));
        expected.add(new ImmutablePair<>(I4, I1));
        expected.add(new ImmutablePair<>(I4, I3));

        assertEquals(expected, findIntersects(Arrays.asList(I1, I2, I3, I4)));
    }
}
