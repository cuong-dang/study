package com.cuongd.study.ooad.c1;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/*
 * Name: Cuong Dang
 * Project: Project 1 - Java Refresher
 * Class: OOAD Course 1
 * Release date: September 26, 2025
 */
class Generator { // Example: class definition
  private final String[] randomizerNames = {
    "", "Random", "Math", "ThreadLocalRandom"
  }; // Example: class attribute (technically, this can be a local variable in execute as well)

  public void execute() { // Example: method definition

    for (int i = 1; i <= 3; i++) {
      System.out.println("From " + randomizerNames[i] + ":");
      display(statistics(populate(10, i)), true);
      display(statistics(populate(100, i)), false);
      display(statistics(populate(1000, i)), false);
    }
  }

  private static ArrayList<Double> populate(int n, int randNumGen) { // Example: method definition
    Random rand = new Random();
    ThreadLocalRandom tlr = ThreadLocalRandom.current();
    Supplier<Double> randomizer =
        () ->
            switch (randNumGen) {
              case 1 -> rand.nextDouble();
              case 2 -> Math.random();
              case 3 -> tlr.nextDouble();
              default -> throw new AssertionError();
            };

    ArrayList<Double> result = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      result.add(randomizer.get());
    }
    return result;
  }

  private ArrayList<Double> statistics(
      ArrayList<Double> randomValues) { // Example: method definition
    double n = randomValues.size();
    DoubleSummaryStatistics stats =
        randomValues.stream()
            .collect(
                DoubleSummaryStatistics::new,
                DoubleSummaryStatistics::accept,
                DoubleSummaryStatistics::combine);
    double variance =
        randomValues.stream().map(x -> Math.pow(x - stats.getAverage(), 2)).reduce(0.0, Double::sum)
            / n;
    double sd = Math.sqrt(variance);

    ArrayList<Double> result = new ArrayList<>();
    result.add(n);
    result.add(stats.getAverage());
    result.add(sd);
    result.add(stats.getMin());
    result.add(stats.getMax());
    return result;
  }

  private void display(
      ArrayList<Double> results, boolean headerOn) { // Example: accessibility (private)
    if (headerOn) {
      System.out.printf("%-6s%-12s%-12s%-12s%-12s\n", "n", "Mean", "StdDev", "Min", "Max");
    }
    System.out.printf(
        "%-6.0f%-12.4f%-12.4f%-12.4f%-12.4f\n",
        results.get(0), results.get(1), results.get(2), results.get(3), results.get(4));
  }

  static void main(String[] args) {
    Generator g = new Generator(); // Example: instantiation
    g.execute();
  }
}
