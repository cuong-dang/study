package com.cuongd.study.alspe.c3.pa3;

class PathWis {
    private double[] weight;
    private double[] dp;
    private int[] included;

    public PathWis(int N) {
        weight = new double[N];
        dp = new double[N];
        included = new int[N];
    }

    public void setWeight(int i, double weight) {
        this.weight[i] = weight;
    }

    public void solve() {
        dp[0] = weight[0];
        dp[1] = Math.max(dp[0], weight[1]);
        for (int i = 2; i < dp.length; i++) {
            dp[i] = Math.max(dp[i - 1], weight[i] + dp[i - 2]);
        }
        reconstruct();
    }

    public int isIncluded(int i) {
        return included[i];
    }

    private void reconstruct() {
        int i = dp.length - 1;
        while (i >= 2) {
            if (Double.compare(dp[i - 1], weight[i] + dp[i - 2]) >= 0) {
                i -= 1;
            } else {
                included[i] = 1;
                i -= 2;
            }
        }
        if (included[2] == 1 || dp[1] == weight[0]) {
            included[0] = 1;
        } else {
            included[1] = 1;
        }
    }

    public static void main(String[] args) {
        PathWis wis = new PathWis(6);
        wis.setWeight(0, 3);
        wis.setWeight(1, 2);
        wis.setWeight(2, 1);
        wis.setWeight(3, 6);
        wis.setWeight(4, 4);
        wis.setWeight(5, 5);
        wis.solve();
        assert wis.dp[0] == 3;
        assert wis.dp[1] == 3;
        assert wis.dp[2] == 4;
        assert wis.dp[3] == 9;
        assert wis.dp[4] == 9;
        assert wis.dp[5] == 14;

        assert wis.included[0] == 1;
        assert wis.included[1] == 0;
        assert wis.included[2] == 0;
        assert wis.included[3] == 1;
        assert wis.included[4] == 0;
        assert wis.included[5] == 1;

        wis = new PathWis(4);
        wis.setWeight(0, 1);
        wis.setWeight(1, 4);
        wis.setWeight(2, 5);
        wis.setWeight(3, 4);
        wis.solve();
        assert wis.dp[0] == 1;
        assert wis.dp[1] == 4;
        assert wis.dp[2] == 6;
        assert wis.dp[3] == 8;

        assert wis.included[0] == 0;
        assert wis.included[1] == 1;
        assert wis.included[2] == 0;
        assert wis.included[3] == 1;
    }
}
