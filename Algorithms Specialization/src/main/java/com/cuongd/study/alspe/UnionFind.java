package com.cuongd.study.alspe;

public class UnionFind {
    private final int[] parent;
    private final int[] size;

    public UnionFind(int n) {
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public void union(int i, int j) {
        int p1 = find(i);
        int p2 = find(j);
        if (p1 == p2) return;
        int demoted = size[p1] < size[p2] ? p1 : p2;
        int promoted = demoted == p1 ? p2 : p1;
        parent[demoted] = promoted;
        size[promoted] += size[demoted];
    }

    public int find(int i) {
        while (parent[i] != i) {
            i = parent[i];
        }
        return i;
    }

    public static void main(String[] args) {
        UnionFind uf = new UnionFind(6);
        for (int i = 0; i < 6; i++) {
            assert uf.find(i) == i;
            assert uf.parent[i] == i;
            assert uf.size[i] == 1;
        }

        uf.union(0, 1);
        assert uf.find(0) == uf.find(1);
        assert uf.parent[0] == 0;
        assert uf.parent[1] == 0;
        assert uf.size[0] == 2;
        assert uf.size[1] == 1;

        uf.union(2, 1);
        assert uf.parent[0] == 0;
        assert uf.parent[1] == 0;
        assert uf.parent[2] == 0;
        assert uf.size[0] == 3;
        assert uf.size[1] == 1;
        assert uf.size[2] == 1;

        uf.union(3, 4);
        uf.union(3, 5);
        assert uf.parent[3] == 3;
        assert uf.parent[4] == 3;
        assert uf.parent[5] == 3;
        assert uf.size[3] == 3;
        assert uf.size[4] == 1;
        assert uf.size[5] == 1;

        uf.union(2, 5);
        assert uf.parent[0] == 0;
        assert uf.parent[1] == 0;
        assert uf.parent[2] == 0;
        assert uf.parent[3] == 0;
        assert uf.parent[4] == 3;
        assert uf.parent[5] == 3;
        assert uf.size[0] == 6;
        assert uf.size[1] == 1;
        assert uf.size[2] == 1;
        assert uf.size[3] == 3;
        assert uf.size[4] == 1;
        assert uf.size[5] == 1;
    }
}
