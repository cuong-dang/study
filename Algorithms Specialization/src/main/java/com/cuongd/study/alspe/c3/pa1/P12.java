package com.cuongd.study.alspe.c3.pa1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class P12 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("data/c3pa1p12.txt"));
        List<Job> jobs = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] split = lines.get(i).split("\\s+");
            int weight = Integer.parseInt(split[0]), length = Integer.parseInt(split[1]);
            jobs.add(new Job(weight, length));
        }

        Scheduler diffScheduler = new Scheduler(jobs, (j1, j2) -> {
            int diff1 = j1.weight - j1.length, diff2 = j2.weight - j2.length;
            if (diff1 == diff2) {
                return Integer.compare(j2.weight, j1.weight);
            }
            return Integer.compare(diff2, diff1);
        });
        Scheduler ratioScheduler = new Scheduler(jobs,
                Comparator.comparingDouble((Job j) -> (double) j.weight / j.length).reversed()
        );
        System.out.println("Diff weighted sum: " + weightedCompletions(diffScheduler));
        System.out.println("Ratio weighted sum: " + weightedCompletions(ratioScheduler));
    }

    private static long weightedCompletions(Scheduler s) {
        long sum = 0, time = 0;
        while (s.hasNext()) {
            Job next = s.next();
            time += next.length;
            sum += next.weight * time;
        }
        return sum;
    }
}
