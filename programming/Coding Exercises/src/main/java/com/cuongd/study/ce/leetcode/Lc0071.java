package com.cuongd.study.ce.leetcode;

import java.util.ArrayList;
import java.util.List;

public class Lc0071 {
  public String simplifyPath(String path) {
    String[] ents = path.split("/");
    Stack<String> result = new Stack<>();
    for (String ent : ents) {
      switch (ent) {
        case ".":
          continue;
        case "..":
          if (result.size() == 0) continue;
          result.pop();
          break;
        case "":
          continue;
        default:
          result.push(ent);
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("/");
    result
        .toListInOrder()
        .forEach(
            e -> {
              sb.append(e);
              sb.append("/");
            });
    if (sb.length() > 1) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  private static class Stack<E> {
    private List<E> a;
    private int n;

    public Stack() {
      a = new ArrayList<>();
    }

    public void push(E e) {
      if (a.size() == n) {
        a.add(e);
      } else {
        a.set(n, e);
      }
      n++;
    }

    public E pop() {
      if (n < 1) throw new IllegalArgumentException();
      E result = a.get(--n);
      a.set(n, null);
      return result;
    }

    public int size() {
      return n;
    }

    public List<E> toListInOrder() {
      List<E> result = new ArrayList<>();
      for (int i = 0; i < n; i++) {
        result.add(a.get(i));
      }
      return result;
    }
  }

  public static void main(String[] args) {
    System.out.println(new Lc0071().simplifyPath("/home/"));
    System.out.println(new Lc0071().simplifyPath("/home//foo/"));
  }
}
