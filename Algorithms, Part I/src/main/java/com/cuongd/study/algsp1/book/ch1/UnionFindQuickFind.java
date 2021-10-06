package com.cuongd.study.algsp1.book.ch1;

import java.lang.reflect.InvocationTargetException;

public class UnionFindQuickFind extends UnionFind {
    public UnionFindQuickFind(int N) {
        super(N);
    }

    @Override
    public int find(int p) {
        return id[p];
    }

    @Override
    public void union(int p, int q) {
        int pid = find(p);
        int qid = find(q);

        if (pid == qid) {
            return;
        }
        for (int i = 0; i < id.length; i++) {
            if (id[i] == pid) {
                id[i] = qid;
            }
        }
        count--;
    }

    public static void main(String[] args) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        run(UnionFindQuickFind.class);
    }
}
