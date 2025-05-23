package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.Queue;

public class TrieST<Value> {
    private final static int R = 256;
    private Node root = new Node();

    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

    public Value get(String key) {
        Node x = get(root, key, 0);
        if (x == null) return null;
        return (Value) x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c], key, d+1);
    }

    public void put(String key, Value val) {
        root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) { x.val = val; return x; }
        char c = key.charAt(d);
        x.next[c] = put(x.next[c], key, val, d+1);
        return x;
    }

    public Iterable<String> keys() {
        return keysWithPrefix("");
    }

    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> queue = new Queue<>();
        collect(get(root, prefix, 0), prefix, queue);
        return queue;
    }

    private void collect(Node x, String prefix, Queue<String> queue) {
        if (x == null) return;
        if (x.val != null) queue.enqueue(prefix);
        for (char c = 0; c < R; c++) {
            collect(x.next[c], prefix + c, queue);
        }
    }

    public Iterable<String> keysThatMatch(String pattern) {
        Queue<String> queue = new Queue<>();
        collect(root, "", pattern, queue);
        return queue;
    }

    private void collect(Node x, String prefix, String pattern, Queue<String> queue) {
        if (x == null) return;
        int d = prefix.length();
        if (d == pattern.length()) {
            if (x.val != null) {
                queue.enqueue(prefix);
            }
            return;
        }
        char next = pattern.charAt(d);
        for (char c = 0; c < R; c++) {
            if (next == '.' || c == next) {
                collect(x.next[c], prefix + c, pattern, queue);
            }
        }
    }

    public String longestPrefixOf(String s) {
        int length = search(root, s, 0, 0);
        return s.substring(0, length);
    }

    private int search(Node x, String s, int d, int length) {
        if (x == null) return length;
        if (x.val != null) length = d;
        if (d == s.length()) return length;
        char c = s.charAt(d);
        return search(x.next[c], s, d+1, length);
    }

    public void delete(String key) {
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) {
            x.val = null;
        } else {
            char c = key.charAt(d);
            x.next[c] = delete(x.next[c], key, d+1);
        }
        if (x.val != null) return x;
        for (char c = 0; c < R; c++) {
            if (x.next[c] != null) return x;
        }
        return null;
    }

    public String floor(String s) {
        StringBuilder result = new StringBuilder();
        floor(root, s, 0, result);
        return result.toString();
    }

    private boolean floor(Node x, String s, int d, StringBuilder result) {
        if (d == s.length()) {
            return x.val != null;
        }
        char c = s.charAt(d);
        if (x.next[c] != null) {
            result.append(c);
            if (floor(x.next[c], s, d+1, result)) {
                return true;
            }
            result.deleteCharAt(result.length()-1);
        }
        for (int i = c-1; i >= 0; i--) {
            if (x.next[i] != null) {
                result.append(max(x.next[i], String.format("%c", (char) i)));
                return true;
            }
        }
        return false;
    }

    public String max() {
        return max(root, "");
    }

    private String max(Node x, String key) {
        int next = -1;
        for (int r = R-1; r >= 0; r--) {
            if (x.next[r] != null) {
                next = r;
                break;
            }
        }
        if (next != -1) {
            return max(x.next[next], key + (char) next);
        }
        return key;
    }

    private String min(Node x, String key) {
        if (x.val != null) {
            return key;
        }
        for (int r = 0; r < R; r++) {
            if (x.next[r] != null) {
                return min(x.next[r], key + (char) r);
            }
        }
        return key;
    }

    public int rank(String s) {
        return rank(root, s, 0);
    }

    private int rank(Node x, String s, int d) {
        if (d >= s.length()) return 0;
        char c = s.charAt(d);
        int result = 0;
        for (int i = 0; i < c; i++) {
            result += count(x.next[i]);
        }
        if (x.next[c] != null) {
            result += rank(x.next[c], s, d+1);
        }
        if (x.val != null) {
            result++;
        }
        return result;
    }

    public int size() {
        return count(root);
    }

    private int count(Node x) {
        if (x == null) return 0;
        int result = x.val != null ? 1 : 0;
        for (int i = 0; i < R; i++) {
            result += count(x.next[i]);
        }
        return result;
    }

    public String select(int k) {
        if (root == null) return null;
        return select(root, k, new StringBuilder());
    }

    private String select(Node x, int k, StringBuilder sb) {
        if (k == 0) {
            return min(x, sb.toString());
        }
        if (x.val != null) {
            k--;
        }
        for (int i = 0; i < R; i++) {
            if (x.next[i] != null) {
                int c = count(x.next[i]);
                if (c > k) {
                    sb.append((char) i);
                    return select(x.next[i], k, sb);
                } else {
                    k -= c;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        TrieST<Integer> t = new TrieST<>();
        assert t.size() == 0;
        t.put("she", 0);
        assert t.size() == 1;
        t.put("sells", 1);
        assert t.size() == 2;
        t.put("sea", 2);
        assert t.size() == 3;
        t.put("shells", 3);
        assert t.size() == 4;
        t.put("by", 4);
        assert t.size() == 5;
        t.put("the", 5);
        assert t.size() == 6;
        t.put("sea", 6);
        assert t.size() == 6;
        t.put("shore", 7);
        assert t.size() == 7;

        assert t.max().equals("the");
        assert t.floor("shorf").equals("shore");
        assert t.floor("shells").equals("shells");
        assert t.floor("shellr").equals("sells");
        assert t.floor("shell").equals("sells");
        assert t.floor("shf").equals("shells");

        assert t.rank("the") == 6;
        assert t.rank("thea") == 7;
        assert t.rank("thf") == 7;
        assert t.rank("thd") == 6;
        assert t.rank("s") == 1;
        assert t.rank("sh") == 3;
        assert t.rank("sho") == 5;
        assert t.rank("she") == 3;
        assert t.rank("shel") == 4;
        assert t.rank("shem") == 5;
        assert t.rank("shek") == 4;

        assert t.select(0).equals("by");
        assert t.select(1).equals("sea");
        assert t.select(2).equals("sells");
        assert t.select(3).equals("she");
        assert t.select(4).equals("shells");
        assert t.select(5).equals("shore");
        assert t.select(6).equals("the");
        assert t.select(7) == null;
        t.put("seashore", 8);
        assert t.select(0).equals("by");
        assert t.select(1).equals("sea");
        assert t.select(2).equals("seashore");
        assert t.select(3).equals("sells");
        assert t.select(4).equals("she");
        assert t.select(5).equals("shells");
        assert t.select(6).equals("shore");
        assert t.select(7).equals("the");
    }
}
