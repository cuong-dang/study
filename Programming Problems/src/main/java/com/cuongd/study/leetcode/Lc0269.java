package com.cuongd.study.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Lc0269 {
    private static int R = 256;
    private boolean[] inDict;
    private Map<String, List<Character>> lists;
    private boolean seenPrefix;
    private List<Integer>[] adj;
    private boolean[] isHead;
    private boolean[] marked;
    private boolean[] onStack;
    private boolean hasCycle;
    private Stack<Character> ans;

    public String alienOrder(String[] words) {
        inDict = new boolean[R];
        Arrays.fill(inDict, false);
        lists = new HashMap<>();
        seenPrefix = false;
        preproc(words);
        if (seenPrefix) return "";
        adj = (List<Integer>[]) new List[R];
        for (int i = 0; i < R; i++) {
            adj[i] = new ArrayList<>();
        }
        isHead = new boolean[R];
        Arrays.fill(isHead, true);
        marked = new boolean[R];
        buildGraph();
        hasCycle = false;
        onStack = new boolean[R];
        checkCycle();
        if (hasCycle) return "";
        ans = new Stack<>();
        solve();
        StringBuilder sb = new StringBuilder();
        while (!ans.isEmpty()) {
            sb.append(ans.pop());
        }
        return sb.toString();
    }

    private void preproc(String[] words) {
        for (String word : words) {
            if (lists.containsKey(word)) {
                seenPrefix = true;
                return;
            }
            String prefix = "";
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                inDict[c] = true;
                if (!lists.containsKey(prefix)) {
                    lists.put(prefix, new ArrayList<>());
                }
                List<Character> list = lists.get(prefix);
                if (list.isEmpty() || list.get(list.size() - 1) != c) {
                    list.add(c);
                }
                prefix += c;
            }
        }
    }

    private void buildGraph() {
        for (String prefix : lists.keySet()) {
            List<Character> chain = lists.get(prefix);
            Character v = null;
            for (char c : chain) {
                if (v == null) {
                    v = c;
                    continue;
                }
                isHead[c] = false;
                adj[v].add((int) c);
                v = c;
            }
        }
    }

    private void checkCycle() {
        Arrays.fill(marked, false);
        for (int i = 0; i < R; i++) {
            if (!marked[i]) {
                if (hasCycle) return;
                dfsCycle(i);
            }
        }
    }

    private void dfsCycle(int v) {
        marked[v] = true;
        onStack[v] = true;
        for (int w : adj[v]) {
            if (!marked[w]) {
                dfsCycle(w);
            }
            if (onStack[w]) {
                hasCycle = true;
                return;
            }
        }
        onStack[v] = false;
    }

    private void solve() {
        Arrays.fill(marked, false);
        for (int i = 0; i < R; i++) {
            if (inDict[i] && isHead[i] && !marked[i]) {
                dfsSolve(i);
            }
        }
    }

    private void dfsSolve(int v) {
        marked[v] = true;
        for (int w : adj[v]) {
            if (!marked[w]) {
                dfsSolve(w);
            }
        }
        ans.add((char) v);
    }

    public static void main(String[] args) {
        new Lc0269().alienOrder(new String[]{"wrt", "wrf", "er", "ett", "rftt"});
    }
}
