package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.*;

public class WordNet {
    private final SET<String> nouns;
    private final Digraph G;
    private final ST<String, Integer> st;
    private final String[] synsets;
    private final SAP sap;

    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }
        nouns = new SET<>();
        st = new ST<>();
        /* synsets */
        In synsetsIn = new In(synsets);
        String[] synsetsLines = synsetsIn.readAllLines();
        G = new Digraph(synsetsLines.length);
        this.synsets = new String[synsetsLines.length];
        for (String line : synsetsLines) {
            String[] fields = line.split(",");
            int v = Integer.parseInt(fields[0]);
            for (String noun : fields[1].split(" ")) {
                nouns.add(noun);
                st.put(noun, v);
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
                G.addEdge(src, Integer.parseInt(vertices[i]));
            }
        }
        /* Check isDAG */
        if (new DirectedCycle(G).hasCycle()) {
            throw new IllegalArgumentException();
        }
        sap = new SAP(G);
    }

    public Iterable<String> nouns() {
        return nouns;
    }

    public boolean isNoun(String word) {
        return nouns.contains(word);
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
