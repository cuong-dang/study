package com.cuongd.study.algs.coursera.wk10;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.ST;

public class BaseballElimination {
    private final int n;
    private final String[] teams;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] games;
    private final boolean[] isEliminated;
    private final ST<Integer, SET<String>> certs;
    private final ST<String, Integer> indexByName;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        n = in.readInt();
        teams = new String[n];
        wins = new int[n];
        losses = new int[n];
        remaining = new int[n];
        games = new int[n][n];
        isEliminated = new boolean[n];
        certs = new ST<>();
        indexByName = new ST<>();
        in.readLine();
        for (int i = 0; i < n; i++) {
            String[] s = in.readLine().trim().split("\\s+");
            teams[i] = s[0];
            indexByName.put(s[0], i);
            wins[i] = Integer.parseInt(s[1]);
            losses[i] = Integer.parseInt(s[2]);
            remaining[i] = Integer.parseInt(s[3]);
            for (int j = 4; j < n + 4; j++) {
                games[i][j - 4] = Integer.parseInt(s[j]);
            }
        }
        for (int i = 0; i < n; i++) {
            checkElimination(i);
        }
    }

    // number of teams
    public int numberOfTeams() {
        return n;
    }

    // all teams
    public Iterable<String> teams() {
        return indexByName.keys();
    }

    // number of wins for given team
    public int wins(String team) {
        checkTeam(team);
        return wins[i(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        checkTeam(team);
        return losses[i(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        checkTeam(team);
        return remaining[i(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        checkTeam(team1);
        checkTeam(team2);
        return games[i(team1)][i(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        checkTeam(team);
        return isEliminated[i(team)];
    }

    // subset R of teams that eliminates given team; null if not eliminate
    public Iterable<String> certificateOfElimination(String team) {
        checkTeam(team);
        if (isEliminated[i(team)]) {
            return certs.get(i(team));
        }
        return null;
    }

    private int i(String team) {
        return indexByName.get(team);
    }

    private void checkTeam(String team) {
        if (!indexByName.contains(team)) {
            throw new IllegalArgumentException();
        }
    }

    private void checkElimination(int i) {
        // trivial case
        SET<String> certTeams = new SET<>();
        for (int j = 0; j < n; j++) {
            if (j == i) continue;
            if (wins[i] + remaining[i] < wins[j]) {
                isEliminated[i] = true;
                certTeams.add(teams[j]);
                certs.put(i, certTeams);
                return;
            }
        }

        // maxflow
        int numGames = (n - 1) * (n - 2) / 2;
        FlowNetwork G = new FlowNetwork(2 + numGames + (n - 1));
        double maxflow = 0;
        ST<Integer, Integer> teamVxs = new ST<>();
        ST<Integer, SET<Integer>> gameTeams = new ST<>();
        int gameVx = 1;
        int teamVx = gameVx + numGames;
        for (int j = 0; j < n; j++) {
            if (j == i) continue;
            teamVx = addTeamVx(teamVxs, teamVx, i, j, G);
            int jVx = teamVxs.get(j);
            for (int k = j + 1; k < n; k++) {
                if (k == i) continue;
                teamVx = addTeamVx(teamVxs, teamVx, i, k, G);
                int kVx = teamVxs.get(k);
                FlowEdge e1 = new FlowEdge(0, gameVx, games[j][k]);
                FlowEdge e2 = new FlowEdge(gameVx, jVx, Double.POSITIVE_INFINITY);
                FlowEdge e3 = new FlowEdge(gameVx, kVx, Double.POSITIVE_INFINITY);
                G.addEdge(e1);
                G.addEdge(e2);
                G.addEdge(e3);
                maxflow += games[j][k];
                // participating teams
                SET<Integer> ts = new SET<>();
                ts.add(j);
                ts.add(k);
                gameTeams.put(gameVx, ts);
                gameVx++;
            }
        }
        FordFulkerson ff = new FordFulkerson(G, 0, G.V() - 1);
        isEliminated[i] = Double.compare(ff.value(), maxflow) < 0;
        if (!isEliminated[i]) return;
        for (int j = 1; j < 1 + numGames; j++) {
            if (ff.inCut(j)) {
                gameTeams.get(j).forEach(k -> certTeams.add(teams[k]));
            }
        }
        certs.put(i, certTeams);
    }

    private int addTeamVx(ST<Integer, Integer> teamVxs, int teamVx,
                          int i, int j, FlowNetwork G) {
        if (!teamVxs.contains(j)) {
            teamVxs.put(j, teamVx);
            G.addEdge(new FlowEdge(teamVx, G.V() - 1, wins[i] + remaining[i] - wins[j]));
            return teamVx + 1;
        } else {
            return teamVx;
        }
    }

    public static void main(String[] args) {
        BaseballElimination teams4 = new BaseballElimination("data/baseball/teams4.txt");
        assert teams4.wins("Atlanta") == 83;
        assert teams4.losses("Atlanta") == 71;
        assert teams4.remaining("Atlanta") == 8;
        assert teams4.against("Atlanta", "Atlanta") == 0;
        assert teams4.against("Atlanta", "Philadelphia") == 1;
        assert teams4.against("Atlanta", "New_York") == 6;
        assert teams4.against("Atlanta", "Montreal") == 1;
        assert teams4.isEliminated("Montreal");
        assert teams4.isEliminated("Philadelphia");
        SET<String> certs = new SET<>();
        certs.add("Atlanta");
        certs.add("New_York");
        assert teams4.certificateOfElimination("Philadelphia").equals(certs);

        BaseballElimination teams4a = new BaseballElimination("data/baseball/teams4a.txt");
        assert teams4a.isEliminated("Ghaddafi");

        BaseballElimination teams5 = new BaseballElimination("data/baseball/teams5.txt");
        assert teams5.isEliminated("Detroit");

        BaseballElimination teams7 = new BaseballElimination("data/baseball/teams7.txt");
        assert teams7.isEliminated("Ireland");

        BaseballElimination teams54 = new BaseballElimination("data/baseball/teams54.txt");
        assert teams54.isEliminated("Team3");
        assert teams54.isEliminated("Team29");
        assert teams54.isEliminated("Team37");
        assert teams54.isEliminated("Team50");
    }
}
