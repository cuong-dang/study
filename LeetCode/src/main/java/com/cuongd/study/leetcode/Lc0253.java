package com.cuongd.study.leetcode;

import java.util.Arrays;

public class Lc0253 {
    public int minMeetingRooms(int[][] intervals) {
        MeetingInstant[] ms = new MeetingInstant[intervals.length * 2];
        int i = 0;
        for (int[] interval : intervals) {
            ms[i] = new MeetingInstant(interval[0], SE.START);
            ms[i + 1] = new MeetingInstant(interval[1], SE.END);
            i += 2;
        }
        Arrays.sort(ms, (mi1, mi2) -> {
            int cmp = Integer.compare(mi1.time, mi2.time);
            if (cmp != 0) return cmp;
            if (mi1.type == SE.END) return -1;
            if (mi2.type == SE.END) return 1;
            return 0;
        });
        int ans = 0;
        int k = 0;
        for (int j = 0; j < ms.length; j++) {
            if (ms[j].type == SE.START) {
                k += 1;
                ans = Math.max(ans, k);
            } else {
                k -= 1;
            }
        }
        return ans;
    }

    private static class MeetingInstant {
        public final int time;
        public final SE type;

        public MeetingInstant(int time, SE type) {
            this.time = time;
            this.type = type;
        }
    }

    private enum SE {
        START,
        END
    }
}
