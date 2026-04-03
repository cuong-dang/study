package com.cuongd.study.algs.book.ch1;

import edu.princeton.cs.algs4.Interval1D;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ex122 {
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        double[] input = StdIn.readAllDoubles();

        assert(input.length == 2*N);
        List<Interval1D> intervals = new ArrayList<>();
        for (int i = 0; i < input.length - 1; i += 2)
            intervals.add(new Interval1D(input[i], input[i+1]));
        Set<ImmutablePair<Interval1D, Interval1D>> intersects =
                findIntersects(intervals);
        for (ImmutablePair<Interval1D, Interval1D> intersectPair : intersects)
            StdOut.println(String.format("(%s, %s)",
                    intersectPair.left, intersectPair.right));
    }

    /**
     * Find all interval pairs that intersect.
     *
     * @param intervals list of intervals; must have more than one element
     * @return list of pairs that intersect
     */
    public static Set<ImmutablePair<Interval1D, Interval1D>>
    findIntersects(List<Interval1D> intervals) {
        assert(intervals.size() > 1);
        Set<ImmutablePair<Interval1D, Interval1D>> result = new HashSet<>();

        for (int i = 1; i < intervals.size(); i++)
            for (int j = 0; j < i; j++) {
                Interval1D interval1 = intervals.get(i);
                Interval1D interval2 = intervals.get(j);
                if (interval1.intersects(interval2)) {
                    result.add(new ImmutablePair<>(interval1, interval2));
                }
            }
        return result;
    }
}
