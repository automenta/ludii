// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.nodes;

import collections.FVector;
import collections.FastArrayList;
import search.mcts.MCTS;
import util.Context;
import util.Move;

public final class Node extends BaseNode
{
    protected final Context context;
    protected final Node[] children;
    protected final Move[] legalMoves;
    protected FVector cachedPolicy;
    protected final int[] childIndices;
    
    public Node(final MCTS mcts, final BaseNode parent, final Move parentMove, final Move parentMoveWithoutConseq, final Context context) {
        super(mcts, parent, parentMove, parentMoveWithoutConseq, context.game());
        this.cachedPolicy = null;
        this.context = context;
        if (context.trial().over()) {
            this.legalMoves = new Move[0];
        }
        else {
            final FastArrayList<Move> actions = context.game().moves(context).moves();
            actions.toArray(this.legalMoves = new Move[actions.size()]);
        }
        this.children = new Node[this.legalMoves.length];
        this.childIndices = new int[this.children.length];
        for (int i = 0; i < this.childIndices.length; ++i) {
            this.childIndices[i] = i;
        }
    }
    
    @Override
    public void addChild(final BaseNode child, final int moveIdx) {
        this.children[moveIdx] = (Node)child;
    }
    
    @Override
    public Node childForNthLegalMove(final int n) {
        return this.children[n];
    }
    
    @Override
    public Context contextRef() {
        return this.context;
    }
    
    @Override
    public Context deterministicContextRef() {
        return this.context;
    }
    
    @Override
    public Node findChildForMove(final Move move) {
        Node result = null;
        for (final Node child : this.children) {
            if (child != null && child.parentMove().equals(move)) {
                result = child;
                break;
            }
        }
        return result;
    }
    
    @Override
    public FastArrayList<Move> movesFromNode() {
        return new FastArrayList<>(this.legalMoves);
    }
    
    @Override
    public int nodeColour() {
        return this.context.state().mover();
    }
    
    @Override
    public Move nthLegalMove(final int n) {
        return this.legalMoves[n];
    }
    
    @Override
    public int numLegalMoves() {
        return this.children.length;
    }
    
    @Override
    public Context playoutContext() {
        return new Context(this.context);
    }
    
    @Override
    public void rootInit(final Context cont) {
    }
    
    @Override
    public void startNewIteration(final Context cont) {
    }
    
    @Override
    public int sumLegalChildVisits() {
        return this.numVisits;
    }
    
    @Override
    public Context traverse(final int moveIdx) {
        Context newContext;
        if (this.children[moveIdx] == null) {
            newContext = new Context(this.context);
            newContext.game().apply(newContext, this.legalMoves[moveIdx]);
        }
        else {
            newContext = this.children[moveIdx].context;
        }
        return newContext;
    }
    
    @Override
    public void updateContextRef() {
    }
    
    public Node[] children() {
        return this.children;
    }
    
    public Move[] legalActions() {
        return this.legalMoves;
    }
    
    @Override
    public FVector learnedSelectionPolicy() {
        if (this.cachedPolicy == null) {
            this.cachedPolicy = this.mcts.learnedSelectionPolicy().computeDistribution(this.context, new FastArrayList<>(this.legalMoves), true);
        }
        return this.cachedPolicy;
    }
}
