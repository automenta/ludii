// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.selection;

import collections.FVector;
import search.mcts.nodes.BaseNode;

import java.util.concurrent.ThreadLocalRandom;

public final class AG0Selection implements SelectionStrategy
{
    protected double explorationConstant;
    
    public AG0Selection() {
        this(2.5);
    }
    
    public AG0Selection(final double explorationConstant) {
        this.explorationConstant = explorationConstant;
    }
    
    @Override
    public int select(final BaseNode current) {
        int bestIdx = -1;
        double bestValue = Double.NEGATIVE_INFINITY;
        final FVector distribution = current.learnedSelectionPolicy();
        final double parentSqrt = Math.sqrt(current.sumLegalChildVisits());
        int numBestFound = 0;
        final int numChildren = current.numLegalMoves();
        final int mover = current.contextRef().state().mover();
        final double unvisitedValueEstimate = current.valueEstimateUnvisitedChildren(mover, current.contextRef().state());
        for (int i = 0; i < numChildren; ++i) {
            final BaseNode child = current.childForNthLegalMove(i);
            double exploit;
            int numVisits;
            if (child == null) {
                exploit = unvisitedValueEstimate;
                numVisits = 0;
            }
            else {
                exploit = child.averageScore(mover, current.contextRef().state());
                numVisits = child.numVisits();
            }
            final float priorProb = distribution.get(i);
            final double explore = parentSqrt / (1.0 + numVisits);
            final double pucb1Value = exploit + this.explorationConstant * priorProb * explore;
            if (pucb1Value > bestValue) {
                bestValue = pucb1Value;
                bestIdx = i;
                numBestFound = 1;
            }
            else if (pucb1Value == bestValue && ThreadLocalRandom.current().nextInt() % ++numBestFound == 0) {
                bestIdx = i;
            }
        }
        return bestIdx;
    }
    
    @Override
    public int backpropFlags() {
        return 0;
    }
    
    @Override
    public void customise(final String[] inputs) {
        if (inputs.length > 1) {
            for (int i = 1; i < inputs.length; ++i) {
                final String input = inputs[i];
                if (input.startsWith("explorationconstant=")) {
                    this.explorationConstant = Double.parseDouble(input.substring("explorationconstant=".length()));
                }
                else {
                    System.err.println("AG0Selection ignores unknown customization: " + input);
                }
            }
        }
    }
}
