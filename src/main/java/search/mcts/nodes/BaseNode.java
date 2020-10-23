// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.nodes;

import expert_iteration.ExItExperience;
import game.Game;
import gnu.trove.list.array.TIntArrayList;
import collections.FVector;
import collections.FastArrayList;
import policies.softmax.SoftmaxPolicy;
import search.mcts.MCTS;
import util.Context;
import util.Move;
import util.state.State;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseNode
{
    protected BaseNode parent;
    protected final Move parentMove;
    protected final Move parentMoveWithoutConseq;
    protected final MCTS mcts;
    protected int numVisits;
    protected final double[] totalScores;
    protected final Map<MoveKey, NodeStatistics> graveStats;
    
    public BaseNode(final MCTS mcts, final BaseNode parent, final Move parentMove, final Move parentMoveWithoutConseq, final Game game) {
        this.numVisits = 0;
        this.mcts = mcts;
        this.parent = parent;
        this.parentMove = parentMove;
        this.parentMoveWithoutConseq = parentMoveWithoutConseq;
        this.totalScores = new double[game.players().count() + 1];
        final int backpropFlags = mcts.backpropFlags();
        if ((backpropFlags & 0x1) != 0x0) {
            this.graveStats = new HashMap<>();
        }
        else {
            this.graveStats = null;
        }
    }
    
    public abstract void addChild(final BaseNode p0, final int p1);
    
    public abstract BaseNode childForNthLegalMove(final int p0);
    
    public abstract Context contextRef();
    
    public abstract Context deterministicContextRef();
    
    public abstract BaseNode findChildForMove(final Move p0);
    
    public abstract FVector learnedSelectionPolicy();
    
    public abstract FastArrayList<Move> movesFromNode();
    
    public abstract int nodeColour();
    
    public abstract Move nthLegalMove(final int p0);
    
    public abstract int numLegalMoves();
    
    public abstract Context playoutContext();
    
    public abstract void rootInit(final Context p0);
    
    public abstract void startNewIteration(final Context p0);
    
    public abstract int sumLegalChildVisits();
    
    public abstract Context traverse(final int p0);
    
    public abstract void updateContextRef();
    
    public double averageScore(final int player, final State state) {
        return (this.numVisits == 0) ? 0.0 : (this.totalScores[state.playerToAgent(player)] / this.numVisits);
    }
    
    public int numVisits() {
        return this.numVisits;
    }
    
    public BaseNode parent() {
        return this.parent;
    }
    
    public Move parentMove() {
        return this.parentMove;
    }
    
    public void setNumVisits(final int numVisits) {
        this.numVisits = numVisits;
    }
    
    public void setParent(final BaseNode newParent) {
        this.parent = newParent;
    }
    
    public double totalScore(final int player) {
        return this.totalScores[player];
    }
    
    public void update(final double[] utilities) {
        ++this.numVisits;
        for (int p = 1; p < this.totalScores.length; ++p) {
            final double[] totalScores = this.totalScores;
            final int n = p;
            totalScores[n] += utilities[p];
        }
    }
    
    public double valueEstimateUnvisitedChildren(final int player, final State state) {
        switch (this.mcts.qInit()) {
            case DRAW -> {
                return 0.0;
            }
            case INF -> {
                return 10000.0;
            }
            case LOSS -> {
                return -1.0;
            }
            case PARENT -> {
                if (this.numVisits == 0) {
                    return 10000.0;
                }
                return this.averageScore(player, state);
            }
            case WIN -> {
                return 1.0;
            }
            default -> {
                return 0.0;
            }
        }
    }
    
    public NodeStatistics getOrCreateGraveStatsEntry(final MoveKey moveKey) {
        NodeStatistics stats = this.graveStats.get(moveKey);
        if (stats == null) {
            stats = new NodeStatistics();
            this.graveStats.put(moveKey, stats);
        }
        return stats;
    }
    
    public NodeStatistics graveStats(final MoveKey moveKey) {
        return this.graveStats.get(moveKey);
    }
    
    public FVector computeVisitCountPolicy(final double tau) {
        final FVector policy = new FVector(this.numLegalMoves());
        if (tau == 0.0) {
            int maxVisitCount = -1;
            final TIntArrayList maxVisitCountChildren = new TIntArrayList();
            for (int i = 0; i < this.numLegalMoves(); ++i) {
                final BaseNode child = this.childForNthLegalMove(i);
                int visitCount;
                if (child == null) {
                    visitCount = 0;
                }
                else {
                    visitCount = child.numVisits;
                }
                if (visitCount > maxVisitCount) {
                    maxVisitCount = visitCount;
                    maxVisitCountChildren.reset();
                    maxVisitCountChildren.add(i);
                }
                else if (visitCount == maxVisitCount) {
                    maxVisitCountChildren.add(i);
                }
            }
            final float maxProb = 1.0f / maxVisitCountChildren.size();
            for (int j = 0; j < maxVisitCountChildren.size(); ++j) {
                policy.set(maxVisitCountChildren.getQuick(j), maxProb);
            }
        }
        else {
            for (int k = 0; k < this.numLegalMoves(); ++k) {
                final BaseNode child2 = this.childForNthLegalMove(k);
                int visitCount2;
                if (child2 == null) {
                    visitCount2 = 0;
                }
                else {
                    visitCount2 = child2.numVisits;
                }
                policy.set(k, visitCount2);
            }
            if (tau != 1.0) {
                policy.raiseToPower(1.0 / tau);
            }
            final float sumVisits = policy.sum();
            if (sumVisits > 0.0f) {
                policy.mult(1.0f / policy.sum());
            }
        }
        return policy;
    }
    
    public double normalisedEntropy() {
        final FVector distribution = this.computeVisitCountPolicy(1.0);
        final int dim = distribution.dim();
        if (dim <= 1) {
            return 0.0;
        }
        double entropy = 0.0;
        for (int i = 0; i < dim; ++i) {
            final float prob = distribution.get(i);
            if (prob > 0.0f) {
                entropy -= prob * Math.log(prob);
            }
        }
        return entropy / Math.log(dim);
    }
    
    public double learnedSelectionPolicyNormalisedEntropy() {
        final FVector distribution = this.learnedSelectionPolicy();
        final int dim = distribution.dim();
        if (dim <= 1) {
            return 0.0;
        }
        double entropy = 0.0;
        for (int i = 0; i < dim; ++i) {
            final float prob = distribution.get(i);
            if (prob > 0.0f) {
                entropy -= prob * Math.log(prob);
            }
        }
        return entropy / Math.log(dim);
    }
    
    public double learnedPlayoutPolicyNormalisedEntropy() {
        final FVector distribution = ((SoftmaxPolicy)this.mcts.playoutStrategy()).computeDistribution(this.contextRef(), this.contextRef().game().moves(this.contextRef()).moves(), true);
        final int dim = distribution.dim();
        if (dim <= 1) {
            return 0.0;
        }
        double entropy = 0.0;
        for (int i = 0; i < dim; ++i) {
            final float prob = distribution.get(i);
            if (prob > 0.0f) {
                entropy -= prob * Math.log(prob);
            }
        }
        return entropy / Math.log(dim);
    }
    
    public ExItExperience generateExItExperience() {
        final FastArrayList<Move> actions = new FastArrayList<>(this.numLegalMoves());
        final float[] valueEstimates = new float[this.numLegalMoves()];
        for (int i = 0; i < this.numLegalMoves(); ++i) {
            final BaseNode child = this.childForNthLegalMove(i);
            actions.add(this.nthLegalMove(i));
            if (child == null) {
                valueEstimates[i] = -1.0f;
            }
            else {
                valueEstimates[i] = (float)child.averageScore(this.deterministicContextRef().state().mover(), this.deterministicContextRef().state());
            }
        }
        return new ExItExperience(new ExItExperience.ExItExperienceState(this.deterministicContextRef()), actions, this.computeVisitCountPolicy(1.0), FVector.wrap(valueEstimates));
    }
    
    public static class NodeStatistics
    {
        public int visitCount;
        public double accumulatedScore;
        
        public NodeStatistics() {
            this.visitCount = 0;
            this.accumulatedScore = 0.0;
        }
        
        @Override
        public String toString() {
            return "[visits = " + this.visitCount + ", accum. score = " + this.accumulatedScore + "]";
        }
    }
    
    public static class MoveKey
    {
        public final Move move;
        private final int cachedHashCode;
        
        public MoveKey(final Move move, final int depth) {
            this.move = move;
            final int prime = 31;
            int result = 1;
            if (move.isPass()) {
                result = 31 * result + depth + 1297;
            }
            else if (move.isSwap()) {
                result = 31 * result + depth + 587;
            }
            else {
                if (!move.isOrientedMove()) {
                    result = 31 * result + (move.toNonDecision() + move.fromNonDecision());
                }
                else {
                    result = 31 * result + move.toNonDecision();
                    result = 31 * result + move.fromNonDecision();
                }
                result = 31 * result + move.stateNonDecision();
            }
            result = 31 * result + move.mover();
            this.cachedHashCode = result;
        }
        
        @Override
        public int hashCode() {
            return this.cachedHashCode;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof MoveKey)) {
                return false;
            }
            final MoveKey other = (MoveKey)obj;
            if (this.move == null) {
                return other.move == null;
            }
            if (this.move.isOrientedMove() != other.move.isOrientedMove()) {
                return false;
            }
            if (this.move.isOrientedMove()) {
                if (this.move.toNonDecision() != other.move.toNonDecision() || this.move.fromNonDecision() != other.move.fromNonDecision()) {
                    return false;
                }
            }
            else {
                boolean fine = false;
                if ((this.move.toNonDecision() == other.move.toNonDecision() && this.move.fromNonDecision() == other.move.fromNonDecision()) || (this.move.toNonDecision() == other.move.fromNonDecision() && this.move.fromNonDecision() == other.move.toNonDecision())) {
                    fine = true;
                }
                if (!fine) {
                    return false;
                }
            }
            return this.move.mover() == other.move.mover() && this.move.stateNonDecision() == other.move.stateNonDecision();
        }
        
        @Override
        public String toString() {
            return "[Move = " + this.move + ", Hash = " + this.cachedHashCode + "]";
        }
    }
}
