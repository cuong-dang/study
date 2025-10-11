package com.cuongd.study.ce.hackerrank;

import java.util.ArrayList;
import java.util.List;

public class ArrayLeftRotation {
  public static List<Integer> rotateLeft(int d, List<Integer> arr) {
    List<Integer> res = new ArrayList<>(arr);
    for (int i = 0; i < arr.size(); ++i) {
      int j = i - d < 0 ? arr.size() + (i - d) : i - d;
      res.set(j, arr.get(i));
    }
    return res;
  }
}
