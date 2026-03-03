package com.cuongd.study.adm.ch1;

import com.cuongd.study.adm.DiGraph;

import java.util.*;

class Ex30 {
  private static class NearestNeighbor {
    private final List<Integer> tour;

    public NearestNeighbor(DiGraph G) {
      tour = new ArrayList<>();
      Map<Integer, PriorityQueue< DiGraph.DiEdge>> pq = new HashMap<>();
      for (int v = 0; v < G.V; ++v) {
        pq.put(v, new PriorityQueue<>(Comparator.comparingDouble(e -> e.weight)));
        for (DiGraph.DiEdge e : G.adj(v)) {
          pq.get(v).add(e);
        }
      }

      int v = 0;
      Set<Integer> seen = new HashSet<>();
      seen.add(v);
      tour.add(v);
      while (seen.size() < G.V) {
        while (true) {
          DiGraph.DiEdge nextEdge = pq.get(v).remove();
          assert nextEdge.from == v;
          int w = nextEdge.to;
          if (seen.contains(w)) continue;
          seen.add(w);
          tour.add(w);
          v = w;
          break;
        }
      }
    }

    public List<Integer> tour() {
      return tour;
    }
  }

  public static void main(String[] args){
    DiGraph G = new DiGraph(4, true);
    G.addEdge(0, 1, 1);
    G.addEdge(0, 2, 2);
    G.addEdge(0, 3, 1);
    G.addEdge(1, 2, 1);
    G.addEdge(1, 3, 2);
    G.addEdge(2, 3, 1);
    assert new NearestNeighbor(G).tour().equals(List.of(0, 1, 2, 3));
  }
}
