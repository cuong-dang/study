package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Queue;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class GraphProperties {
    private final Map<Integer, Integer> eccentricity;
    private int diameter;
    private int radius;
    private int center;
    private int girth = Integer.MAX_VALUE;

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
            } else {
                diameter = max(diameter, vEccentricity);
                radius = min(radius, vEccentricity);
            }
        }

        /* Calculate girth */
        for (int v = 0; v < G.V(); v++) {
            for (int w = 0; w < G.V(); w++) {
                if (v == w || !G.hasEdge(v, w)) {
                    continue;
                }
                boolean[] marked = new boolean[G.V()];
                int[] distTo = new int[G.V()];
                Queue<Integer> q = new Queue<>();

                marked[v] = true;
                q.enqueue(v);
                while (!q.isEmpty()) {
                    int vv = q.dequeue();
                    for (int ww : G.adj(vv)) {
                        if (vv == v && ww == w) {
                            continue;
                        }
                        if (!marked[ww]) {
                            marked[ww] = true;
                            q.enqueue(ww);
                            distTo[ww] = distTo[vv] + 1;
                            if (ww == w) {
                                girth = min(girth, distTo[ww] + 1);
                            }
                        }
                    }
                }
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

    public int girth() {
        return girth;
    }
}
