package com.cuongd.study.algs.book.ch5;

import java.util.ArrayList;
import java.util.List;

public class Ex5330 {
    public static List<Integer> find(char[][] pat, char[][] txt) {
        List<Integer> result = new ArrayList<>();
        RKHash[] patHashes = new RKHash[pat.length];
        for (int i = 0; i < pat.length; i++) {
            RKHash rowHash = new RKHash();
            for (char c : pat[i]) {
                rowHash.append(c);
            }
            patHashes[i] = rowHash;
        }

        RKHash[] leftMostTxtHashes = new RKHash[pat.length];
        for (int i = 0; i < pat.length; i++) {
            RKHash rowHash = new RKHash();
            for (int j = 0; j < pat[0].length; j++) {
                rowHash.append(txt[i][j]);
            }
            leftMostTxtHashes[i] = rowHash;
        }
        for (int i = 0; i <= txt.length - pat.length; i++) {
            RKHash[] txtHashes = new RKHash[pat.length];
            for (int z = 0; z < txtHashes.length; z++) {
                txtHashes[z] = new RKHash();
                txtHashes[z].copy(leftMostTxtHashes[z]);
            }
            for (int j = 0; j <= txt[0].length - pat[0].length; j++) {
                if (same(patHashes, txtHashes)) {
                    result.add(i);
                    result.add(j);
                    return result;
                }
                if (j == txt[0].length - pat[0].length) continue;
                for (int k = 0; k < pat.length; k++) {
                    txtHashes[k].slide(txt[i+k][j], txt[i+k][j+pat[0].length]);
                }
            }
            if (i == txt.length - pat.length) continue;
            for (int k = 1; k < pat.length; k++) {
                leftMostTxtHashes[k-1] = leftMostTxtHashes[k];
            }
            RKHash newRowHash = new RKHash();
            for (int t = 0; t < pat[0].length; t++) {
                newRowHash.append(txt[i+pat.length][t]);
            }
            leftMostTxtHashes[pat.length-1] = newRowHash;
        }
        return result;
    }

    private static boolean same(RKHash[] hs1, RKHash[] hs2) {
        if (hs1.length != hs2.length) return false;
        for (int i = 0; i < hs1.length; i++) {
            if (hs1[i].value() != hs2[i].value()) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        char[][] txt1 = {{'a', 'b', 'c'}, {'d', 'e', 'f'}};
        char[][] pat11 = {{'a', 'b'}, {'d', 'e'}};
        assert find(pat11, txt1).equals(List.of(0, 0));
        char[][] pat12 = {{'b', 'c'}, {'e', 'f'}};
        assert find(pat12, txt1).equals(List.of(0, 1));

        char[][] txt2 = {{'a', 'b', 'c'}, {'d', 'e', 'f'}, {'g', 'h', 'i'}};
        char[][] pat21 = {{'a', 'b', 'c'}, {'d', 'e', 'f'}};
        assert find(pat21, txt2).equals(List.of(0, 0));
        char[][] pat22 = {{'d', 'e'}, {'g', 'h'}};
        assert find(pat22, txt2).equals(List.of(1, 0));
        char[][] pat23 = {{'e', 'f'}, {'h', 'i'}};
        assert find(pat23, txt2).equals(List.of(1, 1));
        char[][] pat24 = {{'b', 'c'}, {'e', 'f'}, {'h', 'i'}};
        assert find(pat24, txt2).equals(List.of(0, 1));
    }
}
