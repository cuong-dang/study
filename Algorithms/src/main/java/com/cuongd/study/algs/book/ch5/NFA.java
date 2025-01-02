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
        regex = rewriteSpecifiedSet(rewriteRange(regex));
        m = regex.length();
        re = regex.toCharArray();
        G = buildEpsilonTransitionDigraph();
    }

    private String rewriteRange(String regex) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < regex.length()) {
            char c = regex.charAt(i);
            if (c == '[' && regex.charAt(i+2) == '-') {
                char rangeStart = regex.charAt(i+1),
                        rangeEnd = regex.charAt(i+3);
                sb.append('(');
                for (char r = rangeStart; r <= rangeEnd; r++) {
                    sb.append(r);
                    sb.append('|');
                }
                sb.deleteCharAt(sb.length()-1);
                sb.append(')');
                i += 4;
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    private String rewriteSpecifiedSet(String regex) {
        StringBuilder sb = new StringBuilder();
        boolean isInBracket = false;
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            if (c == '[') {
                isInBracket = true;
                sb.append('(');
            } else if (c == ']') {
                sb.deleteCharAt(sb.length()-1);
                sb.append(')');
                isInBracket = false;
            } else {
                sb.append(c);
                if (isInBracket) {
                    sb.append('|');
                }
            }
        }
        return sb.toString();
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
            if (re[i] == '(' || re[i] == '*' || re[i] == '+' || re[i] == ')')
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

        nfa = new NFA("(A[BCD]E)");
        assert nfa.recognizes("ABE");
        assert nfa.recognizes("ACE");
        assert nfa.recognizes("ADE");
        assert !nfa.recognizes("AE");
        assert !nfa.recognizes("AFE");
        nfa = new NFA("((A[BCD]*E)");
        assert nfa.recognizes("ABBCDDDE");

        nfa = new NFA("(A[B-D]E)");
        assert nfa.recognizes("ABE");
        assert nfa.recognizes("ACE");
        assert nfa.recognizes("ADE");
        assert !nfa.recognizes("AE");
        assert !nfa.recognizes("AFE");
        nfa = new NFA("((A[B-D]*E)");
        assert nfa.recognizes("ABBCDDDE");
    }
}
