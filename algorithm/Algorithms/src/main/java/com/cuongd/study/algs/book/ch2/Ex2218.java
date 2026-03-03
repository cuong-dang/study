package com.cuongd.study.algs.book.ch2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Ex2218 {
    private static final int N = 10;

    public static <T> Node<T> shuffle(Node<T> list, int n) {
        if (n == 1) {
            list.next = null;
            return list;
        }

        Node<T> mid, left, right;

        mid = seekMid(list, n);
        left = shuffle(list, n/2);
        right = shuffle(mid, n - n/2);
        return shuffleMerge(left, right);
    }

    private static <T> Node<T> seekMid(Node<T> list, int n) {
        Node<T> r;
        int seekElems;

        seekElems = n / 2;
        for (r = list; seekElems > 0; --seekElems)
            r = r.next;
        return r;
    }

    private static <T> Node<T> shuffleMerge(Node<T> left, Node<T> right) {
        Node<T> r, curr;

        if (StdRandom.uniform() < 0.5) {
            curr = right;
            right = right.next;
        } else {
            curr = left;
            left = left.next;
        }
        r = curr;
        while (left != null || right != null) {
            if (left == null || (right != null && StdRandom.uniform() < 0.5)) {
                curr.next = right;
                right = right.next;
            } else {
                curr.next = left;
                left = left.next;
            }
            curr = curr.next;
        }
        curr.next = null;
        return r;
    }

    private static class Node<Item> {
        Item value;
        Node<Item> next;

        @Override
        public String toString() {
            String nextString = next == null? "null" : next.value.toString();
            return "Node{" + "value=" + value + ", next=" + nextString + '}';
        }
    }

    public static void main(String[] args) {
        Node<Integer> list = null;

        for (int i = 0; i < N; ++i) {
            Node<Integer> node = new Node<>();
            node.value = N - i - 1;
            node.next = list;
            list = node;
        }

        list = shuffle(list, N);

        for (Node<Integer> n = list; n != null; n = n.next)
            StdOut.println(n.value);
    }
}
