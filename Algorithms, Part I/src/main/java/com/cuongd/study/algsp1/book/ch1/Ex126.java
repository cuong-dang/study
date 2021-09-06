package com.cuongd.study.algsp1.book.ch1;

public class Ex126 {
    /**
     * Check if a string is a circular rotation of another.
     *
     * @param s1 first string; not null
     * @param s2 second string; not null
     * @return true if `s1` is a circular rotation of `s2`; false otherwise
     */
    static public boolean isCircularRotation(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;
        if (s1.isEmpty())
            return true;
        int s1SplitIdx = s1.indexOf(s2.charAt(0));
        return (s1.substring(s1SplitIdx) + s1.substring(0, s1SplitIdx))
                .equals(s2);
    }
}
