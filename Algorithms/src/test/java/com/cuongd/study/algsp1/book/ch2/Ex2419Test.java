package com.cuongd.study.algsp1.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2419Test {
    @Test
    public void testMaxPQArrayConstructor() {
        MaxPQ<Character> pq = new MaxPQ<>(new Character[]{'S', 'O', 'R', 'T',
                'E', 'X', 'A', 'M', 'P', 'L', 'E'});
        assertEquals((Character) 'X', pq.delMax());
        assertEquals((Character) 'T', pq.delMax());
        assertEquals((Character) 'S', pq.delMax());
        assertEquals((Character) 'R', pq.delMax());
        assertEquals((Character) 'P', pq.delMax());
        assertEquals((Character) 'O', pq.delMax());
        assertEquals((Character) 'M', pq.delMax());
        assertEquals((Character) 'L', pq.delMax());
        assertEquals((Character) 'E', pq.delMax());
        assertEquals((Character) 'E', pq.delMax());
        assertEquals((Character) 'A', pq.delMax());
    }
}
