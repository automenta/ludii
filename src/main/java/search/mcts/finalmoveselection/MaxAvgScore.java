// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.finalmoveselection;

import search.mcts.nodes.BaseNode;
import util.Move;

import java.util.concurrent.ThreadLocalRandom;

public final class MaxAvgScore implements FinalMoveSelectionStrategy
{
    @Override
    public Move selectMove(final BaseNode rootNode) {
        int bestIdx = -1;
        double maxAvgScore = Double.NEGATIVE_INFINITY;
        int numBestFound = 0;
        final int numChildren = rootNode.numLegalMoves();
        final int mover = rootNode.contextRef().state().mover();
        for (int i = 0; i < numChildren; ++i) {
            final BaseNode child = rootNode.childForNthLegalMove(i);
            double avgScore;
            if (child == null) {
                avgScore = rootNode.valueEstimateUnvisitedChildren(mover, rootNode.contextRef().state());
            }
            else {
                avgScore = child.averageScore(mover, rootNode.contextRef().state());
            }
            if (avgScore > maxAvgScore) {
                maxAvgScore = avgScore;
                bestIdx = i;
                numBestFound = 1;
            }
            else if (avgScore == maxAvgScore && ThreadLocalRandom.current().nextInt() % ++numBestFound == 0) {
                bestIdx = i;
            }
        }
        return rootNode.nthLegalMove(bestIdx);
    }
    
    @Override
    public void customize(final String[] inputs) {
    }
}
