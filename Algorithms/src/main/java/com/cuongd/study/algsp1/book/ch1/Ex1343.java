package com.cuongd.study.algsp1.book.ch1;

import java.io.File;

import static edu.princeton.cs.algs4.StdOut.printf;
import static edu.princeton.cs.algs4.StdOut.println;

/** Listing files */
public class Ex1343 {
    private static final String STDINPUT = "./target";

    public static void main(String[] args) {
        walk(STDINPUT, 0);
    }

    private static void walk(String folderpath, int indent_lvl) {
        File folder = new File(folderpath);
        File[] files = folder.listFiles();

        printf(nameWithIndent(indent_lvl, folder));
        for (File file : files) {
            if (file.isDirectory()) {
                walk(file.getAbsolutePath(), indent_lvl + 1);
            } else {
                printf(nameWithIndent(indent_lvl + 1, file));
            }
        }
    }

    private static String nameWithIndent(int indent_lvl, File object) {
        return String.format("%s%s%s%s\n",
                indent_lvl == 0 ? "" : " ".repeat((indent_lvl-1) * 2),
                indent_lvl == 0 ? "" : "|",
                indent_lvl == 0 ? "" : "_",
                object.getName());
    }
}
