package com.cuongd.study.ce.leetcode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class Lc0126 {
  private int V;
  private List<Integer>[] adj;
  private List<Integer>[] pathTo;
  private String[] words;

  public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
    if (!wordList.contains(endWord)) return List.of();
    return solve(beginWord, wordList, endWord);
  }

  private List<List<String>> solve(String begin, List<String> words, String end) {
    V = words.contains(begin) ? words.size() : words.size() + 1;
    adj = (List<Integer>[]) new List[V];
    pathTo = (List<Integer>[]) new List[V];
    for (int i = 0; i < V; i++) {
      adj[i] = new ArrayList<>();
      pathTo[i] = new ArrayList<>();
    }
    this.words = new String[V];
    V = 0;
    Set<String> wordsToAdd = new HashSet<>(words);
    wordsToAdd.remove(begin);
    wordsToAdd.remove(end);
    addWords(Set.of(begin));
    addWords(wordsToAdd);
    addWords(Set.of(end));
    bfs();
    List<List<String>> ans = new ArrayList<>();
    List<String> collector = new ArrayList<>();
    collector.add(end);
    acc(V - 1, collector, ans);
    return ans;
  }

  private void addWords(Set<String> words) {
    for (String word : words) {
      this.words[V++] = word;
      addEdge(word);
    }
  }

  private void addEdge(String word) {
    for (int i = 0; i < V - 1; i++) {
      if (diffByOne(words[i], word)) {
        adj[i].add(V - 1);
        adj[V - 1].add(i);
      }
    }
  }

  private boolean diffByOne(String s, String t) {
    int count = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) != t.charAt(i)) count++;
      if (count == 2) return false;
    }
    if (count == 0) throw new AssertionError();
    return true;
  }

  private void bfs() {
    boolean[] marked = new boolean[V];
    Queue<Integer> q = new ArrayDeque<>();
    marked[0] = true;
    q.add(0);
    while (!q.isEmpty()) {
      Set<Integer> next = new HashSet<>();
      while (!q.isEmpty()) {
        int v = q.remove();
        for (int w : adj[v]) {
          if (marked[w]) continue;
          pathTo[w].add(v);
          next.add(w);
        }
      }
      for (int w : next) {
        marked[w] = true;
        q.add(w);
      }
    }
  }

  private void acc(int from, List<String> collector, List<List<String>> ans) {
    if (from == 0) {
      List<String> anAns = new ArrayList<>();
      for (int i = collector.size() - 1; i >= 0; i--) {
        anAns.add(collector.get(i));
      }
      ans.add(anAns);
      return;
    }
    for (int i : pathTo[from]) {
      collector.add(words[i]);
      acc(i, collector, ans);
      collector.remove(collector.size() - 1);
    }
  }

  public static void main(String[] args) {
    System.out.println(
        new Lc0126().findLadders("hit", "cog", List.of("hot", "dot", "dog", "lot", "log", "cog")));
  }
}
