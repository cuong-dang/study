package com.cuongd.study.algs.book.ch2;

import com.cuongd.study.algs.book.ch1.MyQueue;

/** Linked-list natural mergesort */
public class Ex2217 {
    public static void sortLinkedList(IntNode list) {
        IntNode first = list, last = list, mid, lo = first, hi, oldLoPrev;
        MyQueue<IntNode> boundaries = findMergeBoundaries(list), another = new MyQueue<>(), t;
        LoHi loHi;
        boolean lastSort = false;

        while (last.next != null)
            last = last.next;
        while (true) {
            if (boundaries.size() == 1 && another.isEmpty())
                lastSort = true;
            mid = boundaries.dequeue();
            if (boundaries.isEmpty())
                hi = last;
            else
                hi = boundaries.dequeue();
            oldLoPrev = lo.prev;
            loHi = merge(lo, mid, hi);
            if (lastSort)
                break;
            if (loHi.hi.next == null)
                last = loHi.hi;
            else
                another.enqueue(loHi.hi);
            if (lo == first)
                first = loHi.lo;
            if (oldLoPrev != null)
                oldLoPrev.next = loHi.lo;
            if (boundaries.isEmpty()) {
                t = another;
                another = boundaries;
                boundaries = t;
                lo = first;
            } else
                lo = loHi.hi.next;
        }
    }

    public static LoHi merge(IntNode lo, IntNode mid, IntNode hi) {
        LoHi loHi = new LoHi();
        IntNode curr, currLeft = lo, currRight = mid.next, leftBound = mid.next,
                rightBound = hi.next;

        if (currLeft.value < currRight.value) {
            loHi.lo = currLeft;
            currLeft = currLeft.next;
        }
        else {
            loHi.lo = currRight;
            currRight = currRight.next;
        }
        curr = loHi.lo;
        curr.prev = lo.prev;
        while (currLeft != leftBound || currRight != rightBound) {
            if ((currRight != rightBound) &&
                    (currLeft == mid.next || currLeft.value >= currRight.value)) {
                curr.next = currRight;
                currRight.prev = curr;
                currRight = currRight.next;
            }
            else {
                curr.next = currLeft;
                currLeft.prev = curr;
                currLeft = currLeft.next;
            }
            curr = curr.next;
        }
        curr.next = rightBound;
        if (rightBound != null)
            rightBound.prev = curr;
        loHi.hi = curr;
        return loHi;
    }

    public static MyQueue<IntNode> findMergeBoundaries(IntNode list) {
        MyQueue<IntNode> boundaries = new MyQueue<>();

        for (IntNode p = list; p.next != null; p = p.next)
            if (p.value > p.next.value)
                boundaries.enqueue(p);
        return boundaries;
    }

    public static class IntNode {
        int value;
        IntNode next;
        IntNode prev;

        @Override
        public String toString() {
            String strNext = next == null ? "null" : Integer.toString(next.value);
            String strPrev = prev == null ? "null" : Integer.toString(prev.value);

            return "IntNode{" + "v=" + value + ", n=" + strNext + ", p=" + strPrev + '}';
        }
    }

    public static class LoHi {
        IntNode lo;
        IntNode hi;
    }
}
