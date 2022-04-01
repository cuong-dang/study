package com.cuongd.study.algsp1.book.ch2;

public class Ex2524<Key extends Comparable<Key>> {
    private MaxPQ<KeyWrapper<Key>> pq;
    private int index;

    public Ex2524() {
        pq = new MaxPQ<>();
        index = 0;
    }

    public void insert(Key v) {
        KeyWrapper<Key> kw = new KeyWrapper<>(v, index++);
        pq.insert(kw);
    }

    public Key delMax() {
        return pq.delMax().key;
    }

    public static class KeyWrapper<Key extends Comparable<Key>> implements
            Comparable<KeyWrapper<Key>> {
        public Key key;
        public int index;

        public KeyWrapper(Key key, int index) {
            this.key = key;
            this.index = index;
        }

        @Override
        public int compareTo(KeyWrapper o) {
            if (key.compareTo((Key) o.key) == 0)
                return o.index - index;
            return key.compareTo((Key) o.key);
        }
    }

    private static class TestClass implements Comparable<TestClass> {
        private final int v;
        private final int i;

        public TestClass(int v, int i) {
            this.v = v;
            this.i = i;
        }

        @Override
        public String toString() {
            return "TestClass{" +
                    "v=" + v +
                    ", i=" + i +
                    '}';
        }

        @Override
        public int compareTo(TestClass o) {
            return v - o.v;
        }
    }

    public static void main(String[] args) {
        Ex2524<TestClass> stablePq = new Ex2524<>();
        stablePq.insert(new TestClass(0, 2));
        stablePq.insert(new TestClass(0, 1));
        stablePq.insert(new TestClass(0, 0));
        for (int i = 0; i < 3; ++i) {
            System.out.println(stablePq.delMax());
        }
    }
}
