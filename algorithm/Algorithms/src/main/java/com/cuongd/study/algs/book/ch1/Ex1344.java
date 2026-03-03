package com.cuongd.study.algs.book.ch1;

/** Text editor buffer */
public class Ex1344 {
    private final MyStack<Character> left;
    private final MyStack<Character> right;
    private int N;

    public Ex1344() {
        left = new MyStack<>();
        right = new MyStack<>();
    }

    public void insert(char c) {
        left.push(c);
        N++;
    }

    public char get() {
        return left.peek();
    }

    public void delete() {
        left.pop();
        N--;
    }

    public void left(int k) {
        move(left, right, k);
    }

    public void right(int k) {
        move(right, left, k);
    }

    public int size() {
        return N;
    }

    private void move(MyStack<Character> from, MyStack<Character> to, int k) {
        for (int i = 0; i < k; i++) {
            char c = from.pop();
            to.push(c);
        }
    }
}
