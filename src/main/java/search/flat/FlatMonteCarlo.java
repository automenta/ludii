// 
// Decompiled by Procyon v0.5.36
// 

package search.flat;

import game.Game;
import main.collections.FVector;
import main.collections.FastArrayList;
import util.AI;
import util.Context;
import util.Move;
import util.model.Model;
import utils.AIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FlatMonteCarlo extends AI
{
    protected int player;
    protected int[] lastScoreSums;
    protected int[] lastVisitCounts;
    protected FastArrayList<Move> lastActionList;
    protected double autoPlaySeconds;
    
    public FlatMonteCarlo() {
        this.player = -1;
        this.lastScoreSums = null;
        this.lastVisitCounts = null;
        this.lastActionList = null;
        this.autoPlaySeconds = 0.5;
        this.friendlyName = "Flat MC";
    }
    
    @Override
    public Move selectAction(final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth) {
        final long startTime = System.currentTimeMillis();
        long stopTime = (maxSeconds > 0.0) ? (startTime + (long)(maxSeconds * 1000.0)) : Long.MAX_VALUE;
        final int maxIts = (maxIterations >= 0) ? maxIterations : Integer.MAX_VALUE;
        final FastArrayList<Move> legalMoves = game.moves(context).moves();
        final int numActions = legalMoves.size();
        if (numActions == 1 && this.autoPlaySeconds >= 0.0 && this.autoPlaySeconds < maxSeconds) {
            stopTime = startTime + (long)(this.autoPlaySeconds * 1000.0);
        }
        final int[] sumScores = new int[numActions];
        final int[] numVisits = new int[numActions];
        for (int numIterations = 0; numIterations < maxIts && System.currentTimeMillis() < stopTime; ++numIterations) {
            final Context copyContext = new Context(context);
            final Model model = copyContext.model();
            model.startNewStep(copyContext, null, 1.0, -1, -1, 0.0, false, false, false);
            final int firstAction = ThreadLocalRandom.current().nextInt(numActions);
            model.applyHumanMove(copyContext, legalMoves.get(firstAction), this.player);
            if (!model.isReady()) {
                model.randomStep(copyContext, null, null);
            }
            if (!copyContext.trial().over()) {
                copyContext.game().playout(copyContext, null, 1.0, null, null, 0, -1, -1.0f, ThreadLocalRandom.current());
            }
            final int[] array = numVisits;
            final int n = firstAction;
            ++array[n];
            final double[] utilities = AIUtils.utilities(copyContext);
            final int[] array2 = sumScores;
            final int n2 = firstAction;
            array2[n2] += (int)utilities[this.player];
        }
        final List<Move> bestActions = new ArrayList<>();
        double maxAvgScore = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < numActions; ++i) {
            final double avgScore = sumScores[i] / (double)numVisits[i];
            if (avgScore > maxAvgScore) {
                maxAvgScore = avgScore;
                bestActions.clear();
                bestActions.add(legalMoves.get(i));
            }
            else if (avgScore == maxAvgScore) {
                bestActions.add(legalMoves.get(i));
            }
        }
        this.lastScoreSums = sumScores;
        this.lastVisitCounts = numVisits;
        this.lastActionList = new FastArrayList<>(legalMoves);
        return bestActions.get(ThreadLocalRandom.current().nextInt(bestActions.size()));
    }
    
    @Override
    public void initAI(final Game game, final int playerID) {
        this.player = playerID;
        this.lastScoreSums = null;
        this.lastVisitCounts = null;
        this.lastActionList = null;
    }
    
    public int[] lastScoreSums() {
        return this.lastScoreSums;
    }
    
    public int[] lastVisitCounts() {
        return this.lastVisitCounts;
    }
    
    public FastArrayList<Move> lastActionList() {
        return this.lastActionList;
    }
    
    @Override
    public boolean supportsGame(final Game game) {
        return !game.isDeductionPuzzle();
    }
    
    @Override
    public AIVisualisationData aiVisualisationData() {
        if (this.lastActionList == null) {
            return null;
        }
        final FVector aiDistribution = new FVector(this.lastActionList.size());
        final FVector valueEstimates = new FVector(this.lastActionList.size());
        final FastArrayList<Move> moves = new FastArrayList<>();
        for (int i = 0; i < this.lastActionList.size(); ++i) {
            aiDistribution.set(i, (float)(this.lastScoreSums[i] / (double)this.lastVisitCounts[i]));
            valueEstimates.set(i, (float)(this.lastScoreSums[i] / (double)this.lastVisitCounts[i]));
            moves.add(this.lastActionList.get(i));
        }
        return new AIVisualisationData(aiDistribution, valueEstimates, moves);
    }
}
