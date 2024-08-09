package com.cuongd.study.algs.coursera.wk6;

public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

//    public String outcast(String[] nouns) {
//        int maxDist = 0;
//        String ans = null;
//        for (String noun : nouns) {
//            int d = 0;
//            for (String n : nouns) {
//                d += wordnet.distance(noun, n);
//            }
//            if (d > maxDist) {
//                maxDist = d;
//                ans = noun;
//            }
//        }
//        return ans;
//    }
}
