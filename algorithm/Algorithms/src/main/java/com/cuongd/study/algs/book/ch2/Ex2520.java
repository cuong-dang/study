package com.cuongd.study.algs.book.ch2;

public class Ex2520 {
    public static int longestProcessingTime(Job[] jobs, SortCommon sorter) {
        sorter.sort(jobs);
        Job longestProcessing = null;
        for (int i = 0; i < jobs.length; ++i) {
            Job currJob = jobs[i];
            if (i == 0) {
                longestProcessing = new Job(currJob.startTime, currJob.endTime);
                continue;
            }
            if (currJob.startTime <= longestProcessing.endTime &&
                    currJob.endTime > longestProcessing.endTime) {
                longestProcessing = new Job(longestProcessing.startTime, currJob.endTime);
            } else if (currJob.startTime > longestProcessing.endTime &&
                    currJob.duration > longestProcessing.duration) {
                longestProcessing = currJob;
            }
        }
        assert longestProcessing != null;
        return longestProcessing.duration;
    }

    public static class Job implements Comparable {
        public final int startTime;
        public final int endTime;
        public final int duration;

        public Job(int startTime, int endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = this.endTime - this.startTime;
        }

        @Override
        public int compareTo(Object o) {
            Job that = (Job) o;
            if (this.startTime != that.startTime)
                return this.startTime - that.startTime;
            return this.endTime - that.endTime;
        }
    }
}
