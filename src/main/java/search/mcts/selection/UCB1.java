// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.selection;

import search.mcts.nodes.BaseNode;

import java.util.concurrent.ThreadLocalRandom;

public final class UCB1 implements SelectionStrategy
{
    protected double explorationConstant;
    
    public UCB1() {
        this(Math.sqrt(2.0));
    }
    
    public UCB1(final double explorationConstant) {
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
        for (int i = 0; i < numChildren; ++i) {
            final BaseNode child = current.childForNthLegalMove(i);
            double exploit;
            double explore;
            if (child == null) {
                exploit = unvisitedValueEstimate;
                explore = Math.sqrt(parentLog);
            }
            else {
                exploit = child.averageScore(mover, current.contextRef().state());
                final int numVisits = child.numVisits();
                explore = Math.sqrt(parentLog / numVisits);
            }
            final double ucb1Value = exploit + this.explorationConstant * explore;
            if (ucb1Value > bestValue) {
                bestValue = ucb1Value;
                bestIdx = i;
                numBestFound = 1;
            }
            else if (ucb1Value == bestValue && ThreadLocalRandom.current().nextInt() % ++numBestFound == 0) {
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
                    System.err.println("UCB1 ignores unknown customization: " + input);
                }
            }
        }
    }
}
