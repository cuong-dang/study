package com.cuongd.study.algs.coursera.wk6;

public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {
        Integer maxDist = null;
        String ans = null;
        for (String noun : nouns) {
            int d = 0;
            for (String n : nouns) {
                d += wordnet.distance(noun, n);
            }
            if (maxDist == null || d > maxDist) {
                maxDist = d;
                ans = noun;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        Outcast o = new Outcast(new WordNet("data/synsets.txt", "data/hypernyms.txt"));
        assert o.outcast(new String[]{"horse", "zebra", "cat", "bear", "table"}).equals("table");
        assert o.outcast(new String[]{"water", "soda", "bed", "orange_juice", "milk", "apple_juice",
                "tea", "coffee"}).equals("bed");
        assert o.outcast(new String[]{"apple", "pear", "peach", "banana", "lime", "lemon",
                "blueberry", "strawberry", "mango", "watermelon", "potato"}).equals("potato");
    }
}
