package com.cuongd.study.algs.book.ch5;

import com.cuongd.study.algs.book.ch4.Digraph;
import com.cuongd.study.algs.book.ch4.DirectedDFS;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Stack;

public class NFA {
    private int m;
    private char re[];
    private Digraph G;

    public NFA(String regex) {
        m = regex.length();
        re = regex.toCharArray();
        G = buildEpsilonTransitionDigraph();
    }

    private Digraph buildEpsilonTransitionDigraph() {
        Stack<Integer> ops = new Stack<>();
        Digraph g = new Digraph(m+1);
        for (int i = 0; i < m; i++) {
            int lp = i;
            if (re[i] == '(' || re[i] == '|') {
                ops.push(i);
            } else if (re[i] == ')') {
                int or = ops.pop();
                if (re[or] == '|') {
                    lp = ops.pop();
                    g.addEdge(lp, or+1);
                    g.addEdge(or, i);
                } else {
                    lp = or;
                }
            }
            if (i < m-1 && re[i+1] == '*') {
                g.addEdge(lp, i+1);
                g.addEdge(i+1, lp);
            }
            if (re[i] == '(' || re[i] == '*' || re[i] == ')')
                g.addEdge(i, i+1);
        }
        return g;
    }

    public boolean recognizes(String txt) {
        Bag<Integer> pc = new Bag<>();
        DirectedDFS dfs = new DirectedDFS(G, 0);
        for (int v = 0; v < G.V(); v++) {
            if (dfs.marked(v)) pc.add(v);
        }
        for (int i = 0; i < txt.length(); i++) {
            Bag<Integer> states = new Bag<>();
            for (int v : pc) {
                if (v == m) continue;
                if (re[v] == txt.charAt(i) || re[v] == '.')
                    states.add(v+1);
            }
            dfs = new DirectedDFS(G, states);
            pc = new Bag<>();
            for (int v = 0; v < G.V(); v++) {
                if (dfs.marked(v)) pc.add(v);
            }
        }
        for (int v : pc)
            if (v == m) return true;
        return false;
    }

    public static void main(String[] args) {
        NFA nfa = new NFA("(.*AB((C|D*E)F)*G)");
        assert nfa.recognizes("aABCFDDEFG");
    }
}
