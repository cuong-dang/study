package com.cuongd.study.adm;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.readAllLines;

public class DiGraph {
  public final int V;
  private final boolean isBidirectional;
  private final List<DiEdge>[] adj;

  public DiGraph(int V) {
    this(V, false);
  }

  public DiGraph(int V, boolean isBidirectional) {
    this.V = V;
    this.isBidirectional = isBidirectional;
    adj = new List[V];
    for (int v = 0; v < this.V; v++) {
      adj[v] = new ArrayList<>();
    }
  }

  public void addEdge(int from, int to, double weight) {
    adj[from].add(new DiEdge(from, to, weight));
    if (isBidirectional) {
      adj[to].add(new DiEdge(to, from, weight));
    }
  }

  public List<DiEdge> adj(int v) {
    return adj[v];
  }

  public DiGraph reverse() {
    DiGraph GRev = new DiGraph(V);
    for (int v = 0; v < V; v++) {
      for (DiEdge e : adj(v)) {
        GRev.addEdge(e.to, e.from, e.weight);
      }
    }
    return GRev;
  }

  public static DiGraph fromFile(String filePath) throws IOException {
    List<String> lines = readAllLines(Path.of(filePath));
    int V = Integer.parseInt(lines.get(0).split("\\s+")[0]);
    DiGraph G = new DiGraph(V);
    for (int i = 1; i < lines.size(); i++) {
      String[] split = lines.get(i).split("\\s+");
      int v = Integer.parseInt(split[0]) - 1, w = Integer.parseInt(split[1]) - 1;
      double weight = Double.parseDouble(split[2]);
      G.addEdge(v, w, weight);
    }
    return G;
  }

  public static class DiEdge {
    public final int from;
    public final int to;
    public final double weight;

    public DiEdge(int from, int to) {
      this.from = from;
      this.to = to;
      this.weight = 0;
    }

    public DiEdge(int from, int to, double weight) {
      this.from = from;
      this.to = to;
      this.weight = weight;
    }

    public int other(int v) {
      return v == from ? to : from;
    }
  }
}
