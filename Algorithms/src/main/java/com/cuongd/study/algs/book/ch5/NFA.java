package com.cuongd.study.algs.book.ch5;

import com.cuongd.study.algs.book.ch4.Digraph;
import com.cuongd.study.algs.book.ch4.DirectedDFS;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Stack;

public class NFA {
    private int m;
    private char re[];
    private Digraph G, H;

    public NFA(String regex) {
        m = regex.length();
        re = regex.toCharArray();
        G = buildEpsilonTransitionDigraph();
        H = buildNonEpsilonTransitionDigraph();
    }

    private Digraph buildEpsilonTransitionDigraph() {
        Stack<Integer> ops = new Stack<>();
        Digraph g = new Digraph(m+1);
        for (int i = 0; i < m; i++) {
            int lp = i;
            if (re[i] == '(' || re[i] == '|') {
                ops.push(i);
            } else if (re[i] == ')') {
                Bag<Integer> ors = new Bag<>();
                while (true) {
                    int or = ops.pop();
                    if (re[or] == '|') {
                        ors.add(or);
                    } else {
                        lp = or;
                        break;
                    }
                }
                for (int or : ors) {
                    g.addEdge(lp, or+1);
                    g.addEdge(or, i);
                }
            }
            if (i < m-1) {
                if (re[i+1] == '*') {
                    g.addEdge(lp, i+1);
                    g.addEdge(i+1, lp);
                } else if (re[i+1] == '+') {
                    g.addEdge(i+1, lp);
                }
            }
            if (re[i] == '(' || re[i] == '*' || re[i] == '+' || re[i] == ')' ||
                    re[i] == ']')
                g.addEdge(i, i+1);
        }
        return g;
    }

    private Digraph buildNonEpsilonTransitionDigraph() {
        Digraph h = new Digraph(m+1);
        boolean isInSpecifiedSet = false;
        int lb = 0;
        Bag<Integer> specifiedSet = new Bag<>();
        for (int i = 0; i < m; i++) {
            if (re[i] == '[') {
                isInSpecifiedSet = true;
                lb = i;
                specifiedSet = new Bag<>();
            } else if (re[i] == ']') {
                for (int s : specifiedSet) {
                    h.addEdge(s, i);
                }
                isInSpecifiedSet = false;
            } else if (isInSpecifiedSet) {
                G.addEdge(lb, i);
                specifiedSet.add(i);
            } else if (('A' <= re[i] && re[i] <= 'Z') || re[i] == '.'){
                h.addEdge(i, i+1);
            }
        }
        return h;
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
                    H.adj(v).forEach(states::add);
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

        nfa = new NFA("(A(B|C|D|E)F)");
        assert nfa.recognizes("ABF");
        assert nfa.recognizes("ACF");
        assert nfa.recognizes("ADF");
        assert nfa.recognizes("AEF");
        assert !nfa.recognizes("AF");

        nfa = new NFA("(AB+C)");
        assert nfa.recognizes("ABC");
        assert nfa.recognizes("ABBC");
        assert nfa.recognizes("ABBBC");
        assert !nfa.recognizes("AC");

        nfa = new NFA("(A[BCD]E");
        assert nfa.recognizes("ABE");
        assert nfa.recognizes("ACE");
        assert nfa.recognizes("ADE");
        assert !nfa.recognizes("AE");
        assert !nfa.recognizes("AFE");
        nfa = new NFA("((A([BCD])*E)");
        assert nfa.recognizes("ABBCDDDE");
    }
}
