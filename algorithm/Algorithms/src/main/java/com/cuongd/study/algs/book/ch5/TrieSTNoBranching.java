package com.cuongd.study.algs.book.ch5;

public class TrieSTNoBranching<Value> {
    private final static int R = 256;
    private Node root;

    public void put(String key, Value val) {
        root = put(root, key, val);
    }

    private Node put(Node x, String key, Value val) {
        // null node
        if (x == null) {
            x = new Node(key, val);
            return x;
        }
        // if matching this key
        if (key.equals(x.key)) {
            x.val = val;
            return x;
        }
        // this node's key is a prefix of the new key
        // note: always true for node whose key is empty
        if (key.startsWith(x.key)) {
            if (x.next == null) {
                x.next = new Node[R];
            }
            char c = key.charAt(x.key.length());
            x.next[c] = put(x.next[c], key.substring(x.key.length()+1), val);
            return x;
        }
        // this node's key shares some prefix with the new key
        int diffAt = diffAt(x.key, key);
        assert diffAt < x.key.length();
        // create a new parent node
        Node p = new Node(x.key.substring(0, diffAt), null);
        p.next = new Node[R];
        // put this node as a child
        p.next[x.key.charAt(diffAt)] = x;
        x.key = x.key.substring(diffAt+1);
        // insert new
        // if new key is a prefix
        if (diffAt == key.length()) {
            assert p.key.equals(key);
            p.val = val;
            return p;
        }
        assert diffAt < key.length();
        p.next[key.charAt(diffAt)] = new Node(key.substring(diffAt+1), val);
        return p;
    }

    public Value get(String key) {
        if (key == null || key.isEmpty()) return null;
        Node x = get(root, key);
        if (x == null) return null;
        return (Value) x.val;
    }

    private Node get(Node x, String key) {
        if (x == null) return null;
        if (key.equals(x.key)) return x;
        if (x.next == null) return null;
        if (!x.key.isEmpty()) {
            if (key.startsWith(x.key)) {
                return get(x.next[key.charAt(x.key.length())],
                        key.substring(x.key.length() + 1));
            } else {
                return null;
            }
        }
        return get(x.next[key.charAt(0)], key.substring(1));
    }

    private static int diffAt(String a, String b) {
        int i = 0;
        while (i < a.length() && i < b.length()) {
            if (a.charAt(i) != b.charAt(i)) {
                return i;
            }
            i++;
        }
        return i;
    }

    private static class Node {
        String key;
        Object val;
        Node[] next;

        Node(String key, Object val) {
            this.key = key;
            this.val = val;
        }
    }

    public static void main(String[] args) {
        // test 1 key at root
        TrieSTNoBranching<Integer> t = new TrieSTNoBranching<>();
        assert t.get("a") == null;
        t.put("a", 0);
        t.assertNode(t.root, "a", 0, true);
        assert t.get("a") == 0;
        assert t.get("b") == null;
        // test replacing
        t.put("a", 1);
        t.assertNode(t.root, "a", 1, true);
        assert t.get("a") == 1;

        // test 2 different keys
        t  = new TrieSTNoBranching<>();
        t.put("a", 0);
        t.put("b", 1);
        t.assertNode(t.root, "", null, false);
        t.assertNode(t.root.next['a'], "", 0, true);
        t.assertNode(t.root.next['b'], "", 1, true);
        assert t.get("a") == 0;
        assert t.get("b") == 1;
        assert t.get("c") == null;

        // regression 1
        t  = new TrieSTNoBranching<>();
        t.put("a", 0);
        t.put("ab", 1);
        t.assertNode(t.root, "a", 0, false);
        t.assertNode(t.root.next['b'], "", 1, true);
        assert t.get("a") == 0;
        assert t.get("ab") == 1;
        assert t.get("b") == null;
        assert t.get("ac") == null;
        t.put("b", 2);
        t.assertNode(t.root, "", null, false);
        t.assertNode(t.root.next['a'], "", 0, false);
        t.assertNode(t.root.next['a'].next['b'], "", 1, true);
        t.assertNode(t.root.next['b'], "", 2, true);
        assert t.get("a") == 0;
        assert t.get("ab") == 1;
        assert t.get("b") == 2;
        assert t.get("ac") == null;

        // test insert into a node that is a prefix of the new key
        t  = new TrieSTNoBranching<>();
        t.put("by", 0);
        t.put("she", 1);
        t.assertNode(t.root, "", null, false);
        t.assertNode(t.root.next['b'], "y", 0, true);
        t.assertNode(t.root.next['s'], "he", 1, true);
        t.put("shells", 2);
        t.assertNode(t.root, "", null, false);
        t.assertNode(t.root.next['b'], "y", 0, true);
        t.assertNode(t.root.next['s'], "he", 1, false);
        t.assertNode(t.root.next['s'].next['l'], "ls", 2, true);
        assert t.get("by") == 0;
        assert t.get("she") == 1;
        assert t.get("shells") == 2;
        assert t.get("shell") == null;

        // test insert into a node that starts with the new key
        t  = new TrieSTNoBranching<>();
        t.put("by", 0);
        t.put("shells", 1);
        t.assertNode(t.root, "", null, false);
        t.assertNode(t.root.next['b'], "y", 0, true);
        t.assertNode(t.root.next['s'], "hells", 1, true);
        t.put("she", 2);
        t.assertNode(t.root, "", null, false);
        t.assertNode(t.root.next['b'], "y", 0, true);
        t.assertNode(t.root.next['s'], "he", 2, false);
        t.assertNode(t.root.next['s'].next['l'], "ls", 1, true);
        assert t.get("by") == 0;
        assert t.get("shells") == 1;
        assert t.get("she") == 2;
        assert t.get("shell") == null;
        assert t.get("sh") == null;

        // textbook 1
        t = new TrieSTNoBranching<>();
        t.put("shells", 1);
        t.put("shellfish", 2);
        t.assertNode(t.root, "shell", null, false);
        t.assertNode(t.root.next['s'], "", 1, true);
        t.assertNode(t.root.next['f'], "ish", 2, true);

        // textbook 2
        t = new TrieSTNoBranching<>();
        t.put("she", 0);
        t.assertNode(t.root, "she", 0, true);
        t.put("sells", 1);
        t.assertNode(t.root, "s", null, false);
        t.assertNode(t.root.next['h'], "e", 0, true);
        t.assertNode(t.root.next['e'], "lls", 1, true);
        t.put("sea", 2);
        t.assertNode(t.root.next['e'], "", null, false);
        t.assertNode(t.root.next['e'].next['l'], "ls", 1, true);
        t.assertNode(t.root.next['e'].next['a'], "", 2, true);
        t.put("shells", 3);
        t.assertNode(t.root.next['h'], "e", 0, false);
        t.assertNode(t.root.next['e'].next['l'], "ls", 1, true);
        t.assertNode(t.root.next['e'].next['a'], "", 2, true);
        t.assertNode(t.root.next['h'].next['l'], "ls", 3, true);
        t.put("by", 4);
        t.assertNode(t.root, "", null, false);
        t.assertNode(t.root.next['s'], "", null, false);
        t.assertNode(t.root.next['s'].next['h'], "e", 0, false);
        t.assertNode(t.root.next['s'].next['e'].next['l'], "ls", 1, true);
        t.assertNode(t.root.next['s'].next['e'].next['a'], "", 2, true);
        t.assertNode(t.root.next['s'].next['h'].next['l'], "ls", 3, true);
        t.assertNode(t.root.next['b'], "y", 4, true);
        t.put("the", 5);
        t.assertNode(t.root, "", null, false);
        t.assertNode(t.root.next['s'], "", null, false);
        t.assertNode(t.root.next['s'].next['h'], "e", 0, false);
        t.assertNode(t.root.next['s'].next['e'].next['l'], "ls", 1, true);
        t.assertNode(t.root.next['s'].next['e'].next['a'], "", 2, true);
        t.assertNode(t.root.next['s'].next['h'].next['l'], "ls", 3, true);
        t.assertNode(t.root.next['b'], "y", 4, true);
        t.assertNode(t.root.next['t'], "he", 5, true);
    }

    private void assertNode(Node x, String key, Value val, boolean nextIsNull) {
        assert x.key.equals(key);
        if (val == null) assert x.val == null;
        else assert val.equals(x.val);
        if (nextIsNull) assert x.next == null;
        else assert x.next != null;
    }
}
