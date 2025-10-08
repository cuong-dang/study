package com.cuongd.study.leetcode;

class Lc0042 {
    public int trap(int[] height) {
        int[] h = height;
        if (h.length < 3) {
            return 0;
        }
        int[] maxLefts = new int[h.length], maxRights = new int[h.length];
        maxLefts[0] = maxRights[0] = 0;
        maxLefts[h.length - 1] = maxRights[h.length - 1] = 0;
        buildMax(maxLefts, h, true);
        buildMax(maxRights, h, false);

        int ans = 0;
        for (int i = 1; i < h.length - 1; i++) {
            int level = Math.min(maxLefts[i], maxRights[i]);
            if (level > h[i]) {
                ans += level - h[i];
            }
        }
        return ans;
    }

    private void buildMax(int[] maxes, int[] h, boolean fromLeft) {
        int i = fromLeft ? 1 : h.length - 2;
        int max = fromLeft ? h[0] : h[h.length - 1];
        while (true) {
            int end = fromLeft ? h.length - 1 : 0;
            if (i == end) {
                return;
            }
            int prev = fromLeft ? i - 1 : i + 1;
            max = Math.max(max, h[prev]);
            maxes[i] = max;
            i = fromLeft ? i + 1 : i - 1;
        }
    }

    public static void main(String[] args) {
        assert new Lc0042().trap(
                new int[] { 0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1 }) == 6;
        assert new Lc0042().trap(new int[] { 4, 2, 0, 3, 2, 5 }) == 9;
        assert new Lc0042().trap(new int[] { 4, 2, 3 }) == 1;
        assert new Lc0042().trap(new int[] { 0 }) == 0;
    }
}
