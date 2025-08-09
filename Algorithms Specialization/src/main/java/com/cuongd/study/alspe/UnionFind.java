package com.cuongd.study.alspe;

public class UnionFind {
    private final int[] parent;
    private final int[] rank;

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public void union(int i, int j) {
        int p1 = find(i);
        int p2 = find(j);
        if (p1 == p2) return;
        int demoted = rank[p1] < rank[p2] ? p1 : p2;
        int promoted = demoted == p1 ? p2 : p1;
        boolean increasingRank = rank[p1] == rank[p2];
        parent[demoted] = promoted;
        if (increasingRank) {
            rank[promoted]++;
        }
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
            assert uf.rank[i] == 0;
        }

        uf.union(0, 1);
        assert uf.find(0) == uf.find(1);
        assert uf.parent[0] == 0;
        assert uf.parent[1] == 0;
        assert uf.rank[0] == 1;
        assert uf.rank[1] == 0;

        uf.union(2, 1);
        assert uf.parent[0] == 0;
        assert uf.parent[1] == 0;
        assert uf.parent[2] == 0;
        assert uf.rank[0] == 1;
        assert uf.rank[1] == 0;
        assert uf.rank[2] == 0;

        uf.union(3, 4);
        uf.union(3, 5);
        assert uf.parent[3] == 3;
        assert uf.parent[4] == 3;
        assert uf.parent[5] == 3;
        assert uf.rank[3] == 1;
        assert uf.rank[4] == 0;
        assert uf.rank[5] == 0;

        uf.union(2, 5);
        assert uf.parent[0] == 0;
        assert uf.parent[1] == 0;
        assert uf.parent[2] == 0;
        assert uf.parent[3] == 0;
        assert uf.parent[4] == 3;
        assert uf.parent[5] == 3;
        assert uf.rank[0] == 2;
        assert uf.rank[1] == 0;
        assert uf.rank[2] == 0;
        assert uf.rank[3] == 1;
        assert uf.rank[4] == 0;
        assert uf.rank[5] == 0;
    }
}
