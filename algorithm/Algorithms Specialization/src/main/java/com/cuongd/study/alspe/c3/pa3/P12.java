package com.cuongd.study.alspe.c3.pa3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.Files.readAllLines;

class P12 {
    public static void main(String[] args) throws IOException {
        List<String> lines = readAllLines(Path.of("data/c3pa3p12.txt"));
        HuffmanEncoder<Integer> he = new HuffmanEncoder<>();
        for (int i = 1; i < lines.size(); i++) {
            double weight = Double.parseDouble(lines.get(i));
            he.add(i, weight);
        }
        he.encode();
        int p1Ans = Integer.MIN_VALUE, p2Ans = Integer.MAX_VALUE;
        for (int i = 1; i < lines.size(); i++) {
            String code = he.codeOf(i);
            int len = code.length();
            if (len > p1Ans) {
                p1Ans = len;
            }
            if (len < p2Ans) {
                p2Ans = len;
            }
        }
        System.out.println("P1 ans: " + p1Ans);
        System.out.println("P2 ans: " + p2Ans);
    }
}
