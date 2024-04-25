package com.cuongd.study.algs.book.ch4;

import java.util.HashMap;
import java.util.Map;

public class GraphProperties {
    private final Map<Integer, Integer> eccentricity;
    private int diameter;
    private int radius;
    private int center;

    public GraphProperties(Graph G) {
        eccentricity = new HashMap<>();
        diameter = radius = center = 0;
        for (int v = 0; v < G.V(); v++) {
            BreadthFirstPaths bfs = new BreadthFirstPaths(G, v);
            for (int w = 0; w < G.V(); w++) {
                if (w == v) {
                    continue;
                }
                int distTo = bfs.distTo(w);
                if (!eccentricity.containsKey(v)) {
                    eccentricity.put(v, distTo);
                } else if (eccentricity.get(v) < distTo) {
                    eccentricity.put(v, distTo);
                }
            }
        }
        for (int v : eccentricity.keySet()) {
            int vEccentricity = eccentricity.get(v);
            if (diameter == 0) {
                diameter = radius = vEccentricity;
                center = v;
            } else if (diameter < vEccentricity) {
                diameter = vEccentricity;
            } else if (radius > vEccentricity) {
                radius = vEccentricity;
                center = v;
            }
        }
    }

    public int eccentricity(int v) {
        return eccentricity.get(v);
    }

    public int diameter() {
        return diameter;
    }

    public int radius() {
        return radius;
    }

    public int center() {
        return center;
    }
}
