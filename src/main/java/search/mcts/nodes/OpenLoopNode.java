// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.nodes;

import collections.FVector;
import collections.FastArrayList;
import game.Game;
import search.mcts.MCTS;
import util.Context;
import util.Move;

import java.util.ArrayList;
import java.util.List;

public final class OpenLoopNode extends BaseNode
{
    protected final List<OpenLoopNode> children;
    protected Context currentItContext;
    protected Context deterministicContext;
    protected FastArrayList<Move> currentLegalMoves;
    protected FVector learnedSelectionPolicy;
    protected OpenLoopNode[] moveIdxToNode;
    protected float logit;
    
    public OpenLoopNode(final MCTS mcts, final BaseNode parent, final Move parentMove, final Move parentMoveWithoutConseq, final Game game) {
        super(mcts, parent, parentMove, parentMoveWithoutConseq, game);
        this.children = new ArrayList<>(10);
        this.currentItContext = null;
        this.deterministicContext = null;
        this.currentLegalMoves = null;
        this.learnedSelectionPolicy = null;
        this.moveIdxToNode = null;
        this.logit = Float.NaN;
    }
    
    @Override
    public void addChild(final BaseNode child, final int moveIdx) {
        this.children.add((OpenLoopNode)child);
        if (this.parent() == null) {
            this.updateLegalMoveDependencies(true);
        }
    }
    
    @Override
    public OpenLoopNode childForNthLegalMove(final int n) {
        return this.moveIdxToNode[n];
    }
    
    @Override
    public Context contextRef() {
        return this.currentItContext;
    }
    
    @Override
    public Context deterministicContextRef() {
        return this.deterministicContext;
    }
    
    @Override
    public OpenLoopNode findChildForMove(final Move move) {
        OpenLoopNode result = null;
        for (final OpenLoopNode child : this.children) {
            if (child.parentMove().equals(move)) {
                result = child;
                break;
            }
        }
        return result;
    }
    
    @Override
    public FVector learnedSelectionPolicy() {
        return this.learnedSelectionPolicy;
    }
    
    @Override
    public FastArrayList<Move> movesFromNode() {
        return this.currentLegalMoves;
    }
    
    @Override
    public int nodeColour() {
        return 0;
    }
    
    @Override
    public Move nthLegalMove(final int n) {
        return this.currentLegalMoves.get(n);
    }
    
    @Override
    public int numLegalMoves() {
        return this.currentLegalMoves.size();
    }
    
    @Override
    public Context playoutContext() {
        return this.currentItContext;
    }
    
    @Override
    public void rootInit(final Context context) {
        this.deterministicContext = context;
        this.currentItContext = new Context(context);
        this.updateLegalMoveDependencies(true);
    }
    
    @Override
    public void startNewIteration(final Context context) {
        this.currentItContext = new Context(context);
    }
    
    @Override
    public int sumLegalChildVisits() {
        int sum = 0;
        for (int i = 0; i < this.numLegalMoves(); ++i) {
            final OpenLoopNode child = this.childForNthLegalMove(i);
            if (child != null) {
                sum += child.numVisits;
            }
        }
        return sum;
    }
    
    @Override
    public Context traverse(final int moveIdx) {
        this.currentItContext.game().apply(this.currentItContext, this.currentLegalMoves.get(moveIdx));
        return this.currentItContext;
    }
    
    @Override
    public void updateContextRef() {
        this.currentItContext = this.parent.contextRef();
        this.updateLegalMoveDependencies(false);
    }
    
    private void updateLegalMoveDependencies(final boolean root) {
        final Context context = root ? this.deterministicContext : this.currentItContext;
        this.currentLegalMoves = new FastArrayList<>(context.game().moves(context).moves());
        if (root) {
            int i = 0;
            while (i < this.children.size()) {
                if (this.currentLegalMoves.contains(this.children.get(i).parentMoveWithoutConseq)) {
                    ++i;
                }
                else {
                    this.children.remove(i);
                }
            }
        }
        this.moveIdxToNode = new OpenLoopNode[this.currentLegalMoves.size()];
        for (int i = 0; i < this.moveIdxToNode.length; ++i) {
            final Move move = this.currentLegalMoves.get(i);
            for (OpenLoopNode child : this.children) {
                if (move.equals(child.parentMoveWithoutConseq)) {
                    this.moveIdxToNode[i] = child;
                    break;
                }
            }
        }
        if (this.mcts.learnedSelectionPolicy() != null) {
            final float[] logits = new float[this.moveIdxToNode.length];
            for (int k = 0; k < logits.length; ++k) {
                if (this.moveIdxToNode[k] != null && !Float.isNaN(this.moveIdxToNode[k].logit)) {
                    logits[k] = this.moveIdxToNode[k].logit;
                }
                else {
                    logits[k] = this.mcts.learnedSelectionPolicy().computeLogit(context, this.currentLegalMoves.get(k));
                    if (this.moveIdxToNode[k] != null) {
                        this.moveIdxToNode[k].logit = logits[k];
                    }
                }
            }
            (this.learnedSelectionPolicy = FVector.wrap(logits)).softmax();
        }
    }
}
