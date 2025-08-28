package com.cuongd.study.alspe.c3.pa3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.Files.readAllLines;

class P3 {
    public static void main(String[] args) throws IOException {
        List<String> lines = readAllLines(Path.of("data/c3pa3p3.txt"));
        PathWis wis = new PathWis(Integer.parseInt(lines.get(0)));
        for (int i = 1; i < lines.size(); i++) {
            wis.setWeight(i - 1, Double.parseDouble(lines.get(i)));
        }
        wis.solve();
        for (int i : List.of(1, 2, 3, 4, 17, 117, 517, 997)) {
            System.out.print(wis.isIncluded(i - 1));
        }
    }
}
