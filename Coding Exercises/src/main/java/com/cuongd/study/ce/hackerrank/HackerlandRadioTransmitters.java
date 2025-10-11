package com.cuongd.study.ce.hackerrank;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class HackerlandRadioTransmitters {
  public static int hackerlandRadioTransmitters(List<Integer> x, int k) {
    x.sort(Comparator.comparingInt(i -> i));
    Iterator<Integer> iter = x.iterator();
    int start = iter.next(), res = 0;
    while (true) {
      int put = start;
      // find put position
      while (iter.hasNext()) {
        int next = iter.next();
        if (next - start <= k) {
          put = next;
        } else {
          start = next;
          break;
        }
      }
      ++res;
      // find next uncovered
      while (start <= put + k) {
        if (iter.hasNext()) {
          start = iter.next();
        } else {
          break;
        }
      }
      if (start <= put + k) break;
    }
    return res;
  }

  public static void main(String[] args) {
    assert hackerlandRadioTransmitters(Arrays.asList(1, 2, 3, 4, 5), 1) == 2;
    assert hackerlandRadioTransmitters(Arrays.asList(7, 2, 4, 6, 5, 9, 12, 11), 2) == 3;
    assert hackerlandRadioTransmitters(Arrays.asList(9, 5, 4, 2, 6, 15, 12), 2) == 4;
  }
}
