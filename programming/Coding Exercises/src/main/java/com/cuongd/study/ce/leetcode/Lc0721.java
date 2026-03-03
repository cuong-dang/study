package com.cuongd.study.ce.leetcode;

import static com.cuongd.study.ce.Common.listOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Lc0721 {
  public List<List<String>> accountsMerge(List<List<String>> accounts) {
    Graph G = new Graph();
    for (List<String> account : accounts) {
      G.addVertex(account.get(1));
      for (int i = 2; i < account.size(); i++) {
        G.addEdge(account.get(1), account.get(i));
      }
    }
    DFS dfs = new DFS(G);
    List<List<String>> result = new ArrayList<>(dfs.count);
    List<Set<String>> emails = new ArrayList<>(dfs.count);
    for (int i = 0; i < dfs.count; i++) {
      result.add(null);
      emails.add(null);
    }
    for (List<String> account : accounts) {
      String name = account.get(0);
      int resultIdx = dfs.id.get(account.get(1));
      if (result.get(resultIdx) == null) {
        result.set(resultIdx, new ArrayList<>());
        result.get(resultIdx).add(name);
        emails.set(resultIdx, new HashSet<>());
      }
      for (int i = 1; i < account.size(); i++) {
        emails.get(resultIdx).add(account.get(i));
      }
    }
    for (int i = 0; i < dfs.count; i++) {
      List<String> sorted = new ArrayList<>(emails.get(i));
      sorted.sort(String.CASE_INSENSITIVE_ORDER);
      result.get(i).addAll(sorted);
    }
    return result;
  }

  private static class Graph {
    Map<String, List<String>> adj;
    int V;

    Graph() {
      adj = new HashMap<>();
    }

    void addVertex(String v) {
      if (!adj.containsKey(v)) {
        adj.put(v, new ArrayList<>());
      }
    }

    void addEdge(String v, String w) {
      if (!adj.containsKey(v)) {
        adj.put(v, new ArrayList<>());
      }
      V++;
      if (!adj.containsKey(w)) {
        adj.put(w, new ArrayList<>());
      }
      V++;
      adj.get(v).add(w);
      adj.get(w).add(v);
    }

    List<String> adj(String v) {
      return adj.get(v);
    }
  }

  private static class DFS {
    Map<String, Integer> id;
    Set<String> marked;
    int count;

    DFS(Graph G) {
      count = 0;
      id = new HashMap<>();
      marked = new HashSet<>();
      for (String s : G.adj.keySet()) {
        if (!marked.contains(s)) {
          dfs(G, s);
          count++;
        }
      }
    }

    void dfs(Graph G, String s) {
      id.put(s, count);
      marked.add(s);
      for (String w : G.adj(s)) {
        if (!marked.contains(w)) {
          dfs(G, w);
        }
      }
    }
  }

  public static void main(String[] args) {
    System.out.println(
        new Lc0721()
            .accountsMerge(
                listOf(
                    listOf("John", "johnsmith@mail.com", "john_newyork@mail.com"),
                    listOf("John", "johnsmith@mail.com", "john00@mail.com"),
                    listOf("Mary", "mary@mail.com"),
                    listOf("John", "johnnybravo@mail.com"))));
  }
}
