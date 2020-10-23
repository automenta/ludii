// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.selection;

import main.collections.FVector;
import search.mcts.nodes.BaseNode;

import java.util.concurrent.ThreadLocalRandom;

public final class ExItSelection implements SelectionStrategy
{
    protected double explorationConstant;
    protected double priorPolicyWeight;
    
    public ExItSelection(final double priorPolicyWeight) {
        this(Math.sqrt(2.0), priorPolicyWeight);
    }
    
    public ExItSelection(final double explorationConstant, final double priorPolicyWeight) {
        this.explorationConstant = explorationConstant;
        this.priorPolicyWeight = priorPolicyWeight;
    }
    
    @Override
    public int select(final BaseNode current) {
        int bestIdx = -1;
        double bestValue = Double.NEGATIVE_INFINITY;
        final FVector distribution = current.learnedSelectionPolicy();
        final double parentLog = Math.log(Math.max(1, current.sumLegalChildVisits()));
        int numBestFound = 0;
        final int numChildren = current.numLegalMoves();
        final int mover = current.contextRef().state().mover();
        final double unvisitedValueEstimate = current.valueEstimateUnvisitedChildren(mover, current.contextRef().state());
        for (int i = 0; i < numChildren; ++i) {
            final BaseNode child = current.childForNthLegalMove(i);
            double exploit;
            int numVisits;
            double explore;
            if (child == null) {
                exploit = unvisitedValueEstimate;
                numVisits = 0;
                explore = Math.sqrt(parentLog);
            }
            else {
                exploit = child.averageScore(mover, current.contextRef().state());
                numVisits = child.numVisits();
                explore = Math.sqrt(parentLog / numVisits);
            }
            final float priorProb = distribution.get(i);
            final double priorTerm = priorProb / (numVisits + 1);
            final double ucb1pValue = exploit + this.explorationConstant * explore + this.priorPolicyWeight * priorTerm;
            if (ucb1pValue > bestValue) {
                bestValue = ucb1pValue;
                bestIdx = i;
                numBestFound = 1;
            }
            else if (ucb1pValue == bestValue && ThreadLocalRandom.current().nextInt() % ++numBestFound == 0) {
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
                    System.err.println("ExItSelection ignores unknown customization: " + input);
                }
            }
        }
    }
}
