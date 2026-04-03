package com.cuongd.study.adm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Util {
  public static Set<Set<Integer>> combinations(Set<Integer> set, int k) {
    List<List<Integer>> result = new ArrayList<>();
    combinations(new ArrayList<>(set), null, result, k);
    return result.stream().map(HashSet::new).collect(Collectors.toSet());
  }

  private static void combinations(
      List<Integer> original, List<List<Integer>> working, List<List<Integer>> result, int k) {
    if (k == 0) return;
    List<List<Integer>> newWorking = k == 1 ? result : new ArrayList<>();
    if (working == null) {
      for (int n : original) {
        newWorking.add(List.of(n));
      }
    } else {
      for (List<Integer> partial : working) {
        for (int i = original.indexOf(partial.get(partial.size() - 1)) + 1;
            i < original.size();
            i++) {
          List<Integer> newPartial = new ArrayList<>(partial);
          newPartial.add(original.get(i));
          newWorking.add(newPartial);
        }
      }
    }
    combinations(original, newWorking, result, k - 1);
  }

  public static void main(String[] args) {
    assert combinations(Set.of(0), 0).equals(Set.of());
    assert combinations(Set.of(1), 0).equals(Set.of());
    assert combinations(Set.of(0, 1, 2), 1).equals(Set.of(Set.of(0), Set.of(1), Set.of(2)));
    assert combinations(Set.of(0, 1, 2), 2)
        .equals(Set.of(Set.of(0, 1), Set.of(0, 2), Set.of(1, 2)));
    assert combinations(Set.of(0, 1, 2), 3).equals(Set.of(Set.of(0, 1, 2)));
    assert combinations(Set.of(0, 1, 2, 3), 1)
        .equals(Set.of(Set.of(0), Set.of(1), Set.of(2), Set.of(3)));
    assert combinations(Set.of(0, 1, 2, 3), 2)
        .equals(
            Set.of(
                Set.of(0, 1),
                Set.of(0, 2),
                Set.of(0, 3),
                Set.of(1, 2),
                Set.of(1, 3),
                Set.of(2, 3)));
    assert combinations(Set.of(0, 1, 2, 3), 3)
        .equals(Set.of(Set.of(0, 1, 2), Set.of(0, 1, 3), Set.of(0, 2, 3), Set.of(1, 2, 3)));
  }
}
