package com.cuongd.study.algs.book.ch3;

/**
 * Cuckoo hashing
 */
public class Ex3431<Key, Val> {
    private final LinearProbingST<Key, Val> st1;
    private final LinearProbingST<Key, Val> st2;

    public Ex3431() {
        st1 = new LinearProbingST<>(key -> 11 * key.hashCode() % 16, 16);
        st2 = new LinearProbingST<>(key -> 17 * key.hashCode() % 16, 16);
    }

    public void put(Key key, Val val) {
        Key st1Cuckoo, st2Cuckoo;
        Val st1CuckooVal;

        while (true) {
            st1Cuckoo = st1.hashesTo(key);
            if (st1Cuckoo == null) {
                st1.put(key, val);
                break;
            }
            st1CuckooVal = st1.get(st1Cuckoo);
            st1.delete(st1Cuckoo);
            st1.put(key, val);
            st2Cuckoo = st2.hashesTo(st1Cuckoo);
            if (st2Cuckoo == null) {
                st2.put(st1Cuckoo, st1CuckooVal);
                break;
            }
            val = st2.get(st2Cuckoo);
            st2.delete(st2Cuckoo);
            st2.put(st1Cuckoo, st1CuckooVal);

            if (key.equals(st2Cuckoo)) {
                throw new RuntimeException("Cycle!");
            }
            key = st2Cuckoo;
        }
    }

    public Val get(Key key) {
        Val val = st1.get(key);
        return val != null ? val : st2.get(key);
    }
}
