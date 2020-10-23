// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.finalmoveselection;

import search.mcts.nodes.BaseNode;
import util.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class RobustChild implements FinalMoveSelectionStrategy
{
    @Override
    public Move selectMove(final BaseNode rootNode) {
        final List<Move> bestActions = new ArrayList<>();
        int maxNumVisits = -1;
        for (int numChildren = rootNode.numLegalMoves(), i = 0; i < numChildren; ++i) {
            final BaseNode child = rootNode.childForNthLegalMove(i);
            final int numVisits = (child == null) ? 0 : child.numVisits();
            if (numVisits > maxNumVisits) {
                maxNumVisits = numVisits;
                bestActions.clear();
                bestActions.add(rootNode.nthLegalMove(i));
            }
            else if (numVisits == maxNumVisits) {
                bestActions.add(rootNode.nthLegalMove(i));
            }
        }
        return bestActions.get(ThreadLocalRandom.current().nextInt(bestActions.size()));
    }
    
    @Override
    public void customize(final String[] inputs) {
    }
}
