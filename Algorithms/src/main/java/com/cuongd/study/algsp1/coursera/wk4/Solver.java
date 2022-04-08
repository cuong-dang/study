package com.cuongd.study.algsp1.coursera.wk4;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final Node goalNode;
    private final boolean solvable;
    private final int moves;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        MinPQ<Node> pq = new MinPQ<>();
        MinPQ<Node> pqTwin = new MinPQ<>();
        pq.insert(new Node(initial, 0, null));
        pqTwin.insert(new Node(initial.twin(), 0, null));

        while (true) {
            Node curr = pq.delMin(), currTwin = pqTwin.delMin();
            if (curr.board.isGoal()) {
                solvable = true;
                goalNode = curr;
                moves = curr.moves;
                return;
            } else if (currTwin.board.isGoal()) {
                solvable = false;
                goalNode = null;
                moves = -1;
                return;
            } else {
                for (Board neighbor : curr.board.neighbors())
                    if (curr.prev == null || !neighbor.equals(curr.prev.board))
                        pq.insert(new Node(neighbor, curr.moves + 1, curr));
                for (Board neighborTwin : currTwin.board.neighbors())
                    if (currTwin.prev == null || !neighborTwin.equals(currTwin.prev.board))
                        pqTwin.insert(new Node(neighborTwin, currTwin.moves + 1, currTwin));
            }
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        Stack<Board> solution = new Stack<>();
        for (Node curr = goalNode; curr != null; curr = curr.prev)
            solution.push(curr.board);
        return solution;
    }

    // test client
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private static class Node implements Comparable<Node> {
        public Board board;
        public int moves;
        public int weight;
        public Node prev;

        public Node(Board board, int moves, Node prev) {
            this.board = board;
            this.moves = moves;
            this.weight = board.manhattan() + moves;
            this.prev = prev;
        }

        @Override
        public int compareTo(Node o) {
            return this.weight - o.weight;
        }
    }
}
