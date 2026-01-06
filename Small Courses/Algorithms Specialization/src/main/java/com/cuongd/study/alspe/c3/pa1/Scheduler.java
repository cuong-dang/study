package com.cuongd.study.alspe.c3.pa1;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

class Scheduler implements Iterator<Job> {
    private final PriorityQueue<Job> scheduledJobs;

    public Scheduler(Iterable<Job> jobs, Comparator<? super Job> comp) {
        scheduledJobs = new PriorityQueue<>(comp);
        for (Job j : jobs) {
            scheduledJobs.add(j);
        }
    }

    public boolean hasNext() {
        return !scheduledJobs.isEmpty();
    }

    public Job next() {
        return scheduledJobs.remove();
    }
}
