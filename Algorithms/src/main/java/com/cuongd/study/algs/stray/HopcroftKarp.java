package com.cuongd.study.algs.stray;

import java.util.*;

public class HopcroftKarp<VertexT extends Comparable<VertexT>> {
    private final Graph<VertexT> g;
    private final Set<AbstractMap.SimpleEntry<VertexT, VertexT>> matched;

    public HopcroftKarp(Graph<VertexT> g) {
        this.g = g;
        matched = new HashSet<>();
    }

    public Set<AbstractMap.SimpleEntry<VertexT, VertexT>> match() {
        Map<VertexT, Set<VertexT>> toVerticesMatched = new HashMap<>();
        Set<VertexT> matchedVertices = new HashSet<>();
        Set<VertexT> removedVertices;
        int augmentingPathLength = 1;
        while (augmentingPathLength < g.numEdges) {
            removedVertices = new HashSet<>();
            for (VertexT vertex : g.vertices.keySet()) {
                if (removedVertices.contains(vertex) || matchedVertices.contains(vertex)) {
                    continue;
                }
                List<VertexT> augmentingPath = findAugmentingPath(vertex, augmentingPathLength, toVerticesMatched,
                        matchedVertices, removedVertices);
                if (augmentingPath == null) {
                    continue;
                }
                addAugmentingPath(augmentingPath, toVerticesMatched, matchedVertices);
            }
            augmentingPathLength += 2;
        }
        return matched;
    }

    private List<VertexT> findAugmentingPath(VertexT srcVertex, int targetPathLength,
                                             Map<VertexT, Set<VertexT>> toVerticesMatched,
                                             Set<VertexT> matchedVertices, Set<VertexT> removedVertices) {
        List<VertexT> path = new ArrayList<>();
        path.add(srcVertex);
        if (dfs(path, srcVertex, targetPathLength, false, toVerticesMatched, matchedVertices, removedVertices)) {
            removedVertices.add(srcVertex);
            return path;
        }
        return null;
    }

    private boolean dfs(List<VertexT> path, VertexT vertex, int targetPathLength, boolean matchedEdge,
                        Map<VertexT, Set<VertexT>> toVerticesMatched,
                        Set<VertexT> matchedVertices, Set<VertexT> removedVertices) {
        if (path.size() - 1 == targetPathLength) {
            return !matchedVertices.contains(vertex);
        }
        Set<VertexT> toVertices = g.vertices.get(vertex);
        Set<VertexT> matchedToVertices = toVerticesMatched.getOrDefault(vertex, new HashSet<>());
        for (VertexT toVertex : toVertices) {
            if (removedVertices.contains(toVertex)) {
                continue;
            }
            if ((matchedEdge && matchedToVertices.contains(toVertex)) ||
                    (!matchedEdge && !matchedToVertices.contains(toVertex))) {
                path.add(toVertex);
                removedVertices.add(toVertex);
                if (dfs(path, toVertex, targetPathLength, !matchedEdge, toVerticesMatched,
                        matchedVertices, removedVertices)) {
                    return true;
                }
                path.remove(path.size() - 1);
                removedVertices.remove(toVertex);
            }
        }
        return false;
    }

    private void addAugmentingPath(List<VertexT> path, Map<VertexT, Set<VertexT>> toVerticesMatched, Set<VertexT> matchedVertices) {
        VertexT fromVertex = null;
        boolean toAdd = true;
        for (VertexT augmentingVertex : path) {
            if (fromVertex == null) {
                matchedVertices.add(augmentingVertex);
                fromVertex = augmentingVertex;
                continue;
            }
            if (toAdd) {
                matchedVertices.add(fromVertex);
                matchedVertices.add(augmentingVertex);
                Set<VertexT> matchedVertices1 = toVerticesMatched.getOrDefault(fromVertex, new HashSet<>()),
                        matchedVertices2 = toVerticesMatched.getOrDefault(augmentingVertex, new HashSet<>());
                matchedVertices1.add(augmentingVertex);
                matchedVertices2.add(fromVertex);
                toVerticesMatched.put(fromVertex, matchedVertices1);
                toVerticesMatched.put(augmentingVertex, matchedVertices2);
                if (fromVertex.compareTo(augmentingVertex) < 0) {
                    matched.add(new AbstractMap.SimpleEntry<>(fromVertex, augmentingVertex));
                } else {
                    matched.add(new AbstractMap.SimpleEntry<>(augmentingVertex, fromVertex));
                }
            } else {
                matchedVertices.remove(fromVertex);
                matchedVertices.remove(augmentingVertex);
                Set<VertexT> matchedVertices1 = toVerticesMatched.getOrDefault(fromVertex, new HashSet<>()),
                        matchedVertices2 = toVerticesMatched.getOrDefault(augmentingVertex, new HashSet<>());
                matchedVertices1.remove(augmentingVertex);
                matchedVertices2.remove(fromVertex);
                toVerticesMatched.put(fromVertex, matchedVertices1);
                toVerticesMatched.put(augmentingVertex, matchedVertices2);
                if (fromVertex.compareTo(augmentingVertex) < 0) {
                    matched.remove(new AbstractMap.SimpleEntry<>(fromVertex, augmentingVertex));
                } else {
                    matched.remove(new AbstractMap.SimpleEntry<>(augmentingVertex, fromVertex));
                }
            }
            fromVertex = augmentingVertex;
            toAdd = !toAdd;
        }
    }

    public static class Graph<VertexT> {
        public final Map<VertexT, Set<VertexT>> vertices;
        public int numEdges;

        public Graph() {
            vertices = new HashMap<>();
            numEdges = 0;
        }

        public void addVertex(VertexT v) {
            if (!vertices.containsKey(v)) {
                vertices.put(v, new HashSet<>());
            }
        }

        public void addEdge(VertexT v1, VertexT v2) {
            addVertex(v1);
            addVertex(v2);
            Set<VertexT> v1Vertices = vertices.get(v1), v2Vertices = vertices.get(v2);
            if (!v1Vertices.contains(v2)) {
                ++numEdges;
                v1Vertices.add(v2);
                v2Vertices.add(v1);
            }
        }
    }
}
