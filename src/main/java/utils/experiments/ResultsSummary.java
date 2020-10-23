// 
// Decompiled by Procyon v0.5.36
// 

package utils.experiments;

import game.Game;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import math.Stats;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ResultsSummary
{
    protected final List<String> agents;
    protected Stats[] agentPoints;
    protected Stats[][] agentPointsPerPlayer;
    protected Stats[] agentGameDurations;
    protected Stats[][] agentGameDurationsPerPlayer;
    protected Map<List<String>, double[]> matchupPayoffsMap;
    protected TObjectIntMap<List<String>> matchupCountsMap;
    
    public ResultsSummary(final Game game, final List<String> agents) {
        this.agents = agents;
        final int numPlayers = game.players().count();
        this.agentPoints = new Stats[agents.size()];
        this.agentPointsPerPlayer = new Stats[agents.size()][numPlayers + 1];
        this.agentGameDurations = new Stats[agents.size()];
        this.agentGameDurationsPerPlayer = new Stats[agents.size()][numPlayers + 1];
        for (int i = 0; i < agents.size(); ++i) {
            this.agentPoints[i] = new Stats(agents.get(i) + " points");
            this.agentGameDurations[i] = new Stats(agents.get(i) + " game durations");
            for (int p = 1; p <= numPlayers; ++p) {
                this.agentPointsPerPlayer[i][p] = new Stats(agents.get(i) + " points as P" + p);
                this.agentGameDurationsPerPlayer[i][p] = new Stats(agents.get(i) + " game durations as P" + p);
            }
        }
        this.matchupPayoffsMap = new HashMap<>();
        this.matchupCountsMap = new TObjectIntHashMap<>();
    }
    
    public void recordResults(final int[] agentPermutation, final double[] utilities, final int gameDuration) {
        for (int p = 1; p < agentPermutation.length; ++p) {
            final double points = (utilities[p] + 1.0) / 2.0;
            final int agentNumber = agentPermutation[p];
            this.agentPoints[agentNumber].addSample(points);
            this.agentPointsPerPlayer[agentNumber][p].addSample(points);
            this.agentGameDurations[agentNumber].addSample(gameDuration);
            this.agentGameDurationsPerPlayer[agentNumber][p].addSample(gameDuration);
        }
        final List<String> agentsList = new ArrayList<>(agentPermutation.length - 1);
        for (int p2 = 1; p2 < agentPermutation.length; ++p2) {
            agentsList.add(this.agents.get(agentPermutation[p2]));
        }
        if (!this.matchupPayoffsMap.containsKey(agentsList)) {
            this.matchupPayoffsMap.put(agentsList, new double[utilities.length - 1]);
        }
        this.matchupCountsMap.adjustOrPutValue(agentsList, 1, 1);
        final double[] sumUtils = this.matchupPayoffsMap.get(agentsList);
        for (int p3 = 1; p3 < utilities.length; ++p3) {
            final double[] array = sumUtils;
            final int n = p3 - 1;
            array[n] += utilities[p3];
        }
    }
    
    public double avgScoreForAgentName(final String agentName) {
        double sumScores = 0.0;
        int sumNumGames = 0;
        for (int i = 0; i < this.agents.size(); ++i) {
            if (this.agents.get(i).equals(agentName)) {
                this.agentPoints[i].measure();
                sumScores += this.agentPoints[i].sum();
                sumNumGames += this.agentPoints[i].numSamples();
            }
        }
        return sumScores / sumNumGames;
    }
    
    public String generateIntermediateSummary() {
        final StringBuilder sb = new StringBuilder();
        sb.append("=====================================================\n");
        int totGamesPlayed = 0;
        for (Stats[] stats : this.agentPointsPerPlayer) {
            totGamesPlayed += stats[1].numSamples();
        }
        sb.append("Completed ").append(totGamesPlayed).append(" games.\n");
        sb.append("\n");
        for (int i = 0; i < this.agents.size(); ++i) {
            this.agentPoints[i].measure();
            sb.append(this.agentPoints[i]).append("\n");
            for (int p = 1; p < this.agentPointsPerPlayer[i].length; ++p) {
                this.agentPointsPerPlayer[i][p].measure();
                sb.append(this.agentPointsPerPlayer[i][p]).append("\n");
            }
            if (i < this.agents.size() - 1) {
                sb.append("\n");
            }
        }
        sb.append("=====================================================\n");
        return sb.toString();
    }
    
    public void writeAlphaRankData(final File outFile) {
        try (final PrintWriter writer = new PrintWriter(outFile, StandardCharsets.UTF_8)) {
            writer.write("agents,scores\n");
            for (final Map.Entry<List<String>, double[]> entry : this.matchupPayoffsMap.entrySet()) {
                final List<String> matchup = entry.getKey();
                final StringBuilder agentTuple = new StringBuilder();
                agentTuple.append("\"(");
                for (int i = 0; i < matchup.size(); ++i) {
                    if (i > 0) {
                        agentTuple.append(", ");
                    }
                    agentTuple.append("'");
                    agentTuple.append(matchup.get(i));
                    agentTuple.append("'");
                }
                agentTuple.append(")\"");
                final double[] scoreSums = entry.getValue();
                final int count = this.matchupCountsMap.get(matchup);
                final double[] avgScores = Arrays.copyOf(scoreSums, scoreSums.length);
                for (int j = 0; j < avgScores.length; ++j) {
                    final double[] array = avgScores;
                    final int n = j;
                    array[n] /= count;
                }
                final StringBuilder scoreTuple = new StringBuilder();
                scoreTuple.append("\"(");
                for (int k = 0; k < avgScores.length; ++k) {
                    if (k > 0) {
                        scoreTuple.append(", ");
                    }
                    scoreTuple.append(avgScores[k]);
                }
                scoreTuple.append(")\"");
                writer.write(agentTuple + "," + scoreTuple + "\n");
            }
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
}
