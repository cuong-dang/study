package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.*;

public class WordNet {
    private final ST<String, Bag<Integer>> st;
    private final String[] synsets;
    private final SAP sap;

    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }
        st = new ST<>();
        /* synsets */
        In synsetsIn = new In(synsets);
        String[] synsetsLines = synsetsIn.readAllLines();
        Digraph g = new Digraph(synsetsLines.length);
        this.synsets = new String[synsetsLines.length];
        for (String line : synsetsLines) {
            String[] fields = line.split(",");
            int v = Integer.parseInt(fields[0]);
            for (String noun : fields[1].split(" ")) {
                if (!st.contains(noun)) {
                    st.put(noun, new Bag<>());
                }
                st.get(noun).add(v);
            }
            this.synsets[v] = fields[1];
        }
        /* hypernyms */
        In hypernymsIn = new In(hypernyms);
        while (hypernymsIn.hasNextLine()) {
            String line = hypernymsIn.readLine();
            String[] vertices = line.split(",");
            int src = Integer.parseInt(vertices[0]);
            for (int i = 1; i < vertices.length; i++) {
                g.addEdge(src, Integer.parseInt(vertices[i]));
            }
        }
        /* Check isDAG */
        if (new DirectedCycle(g).hasCycle()) {
            throw new IllegalArgumentException();
        }
        sap = new SAP(g);
    }

    public Iterable<String> nouns() {
        return st.keys();
    }

    public boolean isNoun(String word) {
        return st.contains(word);
    }

    public int distance(String nounA, String nounB) {
        return sap.length(st.get(nounA), st.get(nounB));
    }

    public String sap(String nounA, String nounB) {
        return synsets[sap.ancestor(st.get(nounA), st.get(nounB))];
    }

    public static void main(String[] args) {
        WordNet wn = new WordNet("data/synsets.txt", "data/hypernyms.txt");
        /* isNoun */
        assert wn.isNoun("event");
        assert wn.isNoun("happening");
        assert !wn.isNoun("asdf");
        /* sap */
        assert wn.sap("happening", "miracle").equals("event");
    }
}
