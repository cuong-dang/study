package com.cuongd.study.algsp1.book.ch2;

import java.util.Arrays;
import java.util.List;

public class Ex252 {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("a", "after", "afterthought", "fter", "thought");
        words.sort(String::compareTo);
        System.out.println(words);
        for (String word : words) {
            if (isCompound(word, null, words)) {
                System.out.println(word);
            }
        }
    }

    private static boolean isCompound(String word, String tail, List<String> sortedWords) {
        if (tail == null) {
            tail = getTail(word, sortedWords);
            if (tail == null)
                return false;
            return isCompound(word, tail, sortedWords);
        }

        if (sortedWords.contains(tail))
            return true;
        return isCompound(tail, null, sortedWords);
    }

    private static String getTail(String word, List<String> sortedWords) {
        for (String wordInList : sortedWords) {
            if (word.indexOf(wordInList) == 0 && !word.equals(wordInList)) {
                return word.substring(wordInList.length());
            }
        }
        return null;
    }
}
