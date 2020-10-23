// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.selection;

import search.mcts.nodes.BaseNode;
import util.Move;

import java.util.concurrent.ThreadLocalRandom;

public class UCB1GRAVE implements SelectionStrategy
{
    protected final int ref;
    protected final double bias;
    protected double explorationConstant;
    protected ThreadLocal<BaseNode> currentRefNode;
    
    public UCB1GRAVE() {
        this.currentRefNode = ThreadLocal.withInitial(() -> null);
        this.ref = 100;
        this.bias = 1.0E-5;
        this.explorationConstant = Math.sqrt(2.0);
    }
    
    public UCB1GRAVE(final int ref, final double bias, final double explorationConstant) {
        this.currentRefNode = ThreadLocal.withInitial(() -> null);
        this.ref = ref;
        this.bias = bias;
        this.explorationConstant = explorationConstant;
    }
    
    @Override
    public int select(final BaseNode current) {
        int bestIdx = -1;
        double bestValue = Double.NEGATIVE_INFINITY;
        final double parentLog = Math.log(Math.max(1, current.sumLegalChildVisits()));
        int numBestFound = 0;
        final int numChildren = current.numLegalMoves();
        final int mover = current.contextRef().state().mover();
        final double unvisitedValueEstimate = current.valueEstimateUnvisitedChildren(mover, current.contextRef().state());
        if (this.currentRefNode.get() == null || current.numVisits() > this.ref || current.parent() == null) {
            this.currentRefNode.set(current);
        }
        for (int i = 0; i < numChildren; ++i) {
            final BaseNode child = current.childForNthLegalMove(i);
            double meanScore;
            double meanAMAF;
            double beta;
            double explore;
            if (child == null) {
                meanScore = unvisitedValueEstimate;
                meanAMAF = 0.0;
                beta = 0.0;
                explore = Math.sqrt(parentLog);
            }
            else {
                meanScore = child.averageScore(mover, current.contextRef().state());
                final Move move = child.parentMove();
                final BaseNode.NodeStatistics graveStats = this.currentRefNode.get().graveStats(new BaseNode.MoveKey(move, current.contextRef().trial().numMoves()));
                final double graveScore = graveStats.accumulatedScore;
                final int graveVisits = graveStats.visitCount;
                final int childVisits = child.numVisits();
                meanAMAF = graveScore / graveVisits;
                beta = graveVisits / (graveVisits + childVisits + this.bias * graveVisits * childVisits);
                final int numVisits = child.numVisits();
                explore = Math.sqrt(parentLog / numVisits);
            }
            final double graveValue = (1.0 - beta) * meanScore + beta * meanAMAF;
            final double ucb1GraveValue = graveValue + this.explorationConstant * explore;
            if (ucb1GraveValue > bestValue) {
                bestValue = ucb1GraveValue;
                bestIdx = i;
                numBestFound = 1;
            }
            else if (ucb1GraveValue == bestValue && ThreadLocalRandom.current().nextInt() % ++numBestFound == 0) {
                bestIdx = i;
            }
        }
        if (current.childForNthLegalMove(bestIdx) == null) {
            this.currentRefNode.set(null);
        }
        return bestIdx;
    }
    
    @Override
    public int backpropFlags() {
        return 1;
    }
    
    @Override
    public void customise(final String[] inputs) {
    }
}
