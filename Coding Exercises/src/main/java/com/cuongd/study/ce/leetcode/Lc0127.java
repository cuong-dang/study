package com.cuongd.study.ce.leetcode;

import static com.cuongd.study.ce.Common.listOf;
import static com.cuongd.study.ce.Common.setOf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Lc0127 {
  public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    Map<String, Set<String>> wordToSubs = new HashMap<>();
    wordToSubs.put(beginWord, subsFromWord(beginWord));
    for (String word : wordList) {
      wordToSubs.put(word, subsFromWord(word));
    }
    Map<String, Set<String>> subToWords = buildSubToWords(wordToSubs);

    Set<String> pc = wordToSubs.get(beginWord);
    Set<String> seenSubs = new HashSet<>(pc);
    Set<String> seenWords = new HashSet<>(setOf(beginWord));
    int steps = 1;
    while (!pc.isEmpty()) {
      steps++;
      HashSet<String> newPc = new HashSet<>();
      for (String sub : pc) {
        Set<String> words = subToWords.get(sub);
        if (words.contains(endWord)) {
          return steps;
        }
        for (String word : words) {
          if (seenWords.contains(word)) continue;
          seenWords.add(word);
          Set<String> subs = wordToSubs.get(word);
          for (String newSub : subs) {
            if (seenSubs.contains(newSub)) continue;
            seenWords.add(newSub);
            newPc.add(newSub);
          }
        }
      }
      pc = newPc;
    }
    return 0;
  }

  private Set<String> subsFromWord(String word) {
    Set<String> result = new HashSet<>();
    for (int i = 0; i < word.length(); i++) {
      result.add(word.substring(0, i) + "_" + word.substring(i + 1));
    }
    return result;
  }

  private Map<String, Set<String>> buildSubToWords(Map<String, Set<String>> wordToSubs) {
    Map<String, Set<String>> result = new HashMap<>();
    for (String word : wordToSubs.keySet()) {
      Set<String> subs = wordToSubs.get(word);
      for (String sub : subs) {
        if (!result.containsKey(sub)) {
          result.put(sub, new HashSet<>());
        }
        result.get(sub).add(word);
      }
    }
    return result;
  }

  public static void main(String[] args) {
    System.out.println(
        new Lc0127()
            .ladderLength(
                "talk", "tail", listOf("talk", "tons", "fall", "tail", "gale", "hall", "negs")));
  }
}
