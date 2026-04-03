package com.cuongd.study.adm.ch1;

import static com.cuongd.study.adm.Util.combinations;

import java.util.Set;

class Ex31 {
  private static boolean isCoveredBy(Set<Integer> target, int k, Set<Set<Integer>> sets) {
    Set<Set<Integer>> combinations = combinations(target, k);
    for (Set<Integer> set : sets) {
      for (Set<Integer> covered : combinations(set, k)) {
        combinations.remove(covered);
        if (combinations.isEmpty()) return true;
      }
    }
    return combinations.isEmpty();
  }

  public static void main(String[] args) {
    assert isCoveredBy(Set.of(1, 2, 3, 4, 5), 2, Set.of(Set.of(1, 2, 3), Set.of(1, 4, 5)));
  }
}
