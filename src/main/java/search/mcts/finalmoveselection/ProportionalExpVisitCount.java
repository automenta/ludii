// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.finalmoveselection;

import main.collections.FVector;
import search.mcts.nodes.BaseNode;
import util.Move;

public final class ProportionalExpVisitCount implements FinalMoveSelectionStrategy
{
    protected double tau;
    
    public ProportionalExpVisitCount(final double tau) {
        this.tau = tau;
    }
    
    @Override
    public Move selectMove(final BaseNode rootNode) {
        final FVector distribution = rootNode.computeVisitCountPolicy(this.tau);
        final int actionIndex = distribution.sampleProportionally();
        return rootNode.nthLegalMove(actionIndex);
    }
    
    @Override
    public void customize(final String[] inputs) {
        for (final String input : inputs) {
            if (input.startsWith("tau=")) {
                this.tau = Double.parseDouble(input.substring("tau=".length()));
            }
        }
    }
}
