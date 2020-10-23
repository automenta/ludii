// 
// Decompiled by Procyon v0.5.36
// 

package util.playout;

import game.Game;
import game.rules.phase.Phase;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.state.MoveAgain;
import gnu.trove.list.array.TIntArrayList;
import collections.FVector;
import collections.FastArrayList;
import util.*;
import util.action.others.ActionSwap;

import java.util.List;
import java.util.Random;

public class NoRepetitionPlayout extends Playout
{
    @Override
    public Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random) {
        final Game game = context.game();
        final Phase startPhase = game.rules().phases()[context.state().currentPhase(context.state().mover())];
        final Moves movesRule = startPhase.play().moves();
        int numActionsApplied;
        Trial trial;
        Move move;
        AI ai;
        int mover;
        Phase currPhase;
        Moves legal;
        FeatureSetInterface featureSet;
        FVector weightVector;
        List<TIntArrayList> sparseFeatureVectors;
        float[] logits;
        int i;
        FVector distribution;
        int n;
        Moves legalMoves;
        Moves replay;
        ActionSwap actionSwap;
        Move swapMove;
        FastArrayList<Move> moves;
        int moveIdx;
        for (numActionsApplied = 0, trial = context.trial(); !trial.over() && (maxNumPlayoutActions < 0 || maxNumPlayoutActions > numActionsApplied); ++numActionsApplied) {
            move = null;
            ai = null;
            mover = context.state().mover();
            currPhase = game.rules().phases()[context.state().currentPhase(mover)];
            if (currPhase != startPhase) {
                return game.playout(context, ais, thinkingTime, featureSets, weights, maxNumBiasedActions - numActionsApplied, maxNumPlayoutActions - numActionsApplied, autoPlayThreshold, random);
            }
            if (ais != null) {
                ai = ais.get(context.state().playerToAgent(mover));
            }
            if (ai != null) {
                move = ai.selectAction(game, new Context(context), thinkingTime, -1, -1);
            }
            else if (featureSets != null && (maxNumBiasedActions < 0 || maxNumBiasedActions > numActionsApplied)) {
                legal = game.moves(context);
                if (featureSets.length == 1) {
                    featureSet = featureSets[0];
                    weightVector = weights[0];
                }
                else {
                    featureSet = featureSets[mover];
                    weightVector = weights[mover];
                }
                sparseFeatureVectors = featureSet.computeSparseFeatureVectors(context, legal.moves(), true);
                logits = new float[sparseFeatureVectors.size()];
                for (i = 0; i < sparseFeatureVectors.size(); ++i) {
                    logits[i] = weightVector.dotSparse(sparseFeatureVectors.get(i));
                }
                distribution = FVector.wrap(logits);
                distribution.softmax();
                n = distribution.sampleFromDistribution();
                move = legal.moves().get(n);
            }
            else {
                legalMoves = movesRule.eval(context);
                if (context.game().usesSwapRule() && trial.moveNumber() == context.game().players().count() - 1) {
                    replay = new MoveAgain();
                    actionSwap = new ActionSwap(1, 2);
                    actionSwap.setDecision(true);
                    swapMove = new Move(actionSwap);
                    swapMove.setDecision(true);
                    swapMove.then().add(replay);
                    legalMoves.moves().add(swapMove);
                }
                moves = legalMoves.moves();
                while (!moves.isEmpty()) {
                    moveIdx = random.nextInt(moves.size());
                    move = moves.removeSwap(moveIdx);
                    if (Game.satisfiesStateComparison(context, move)) {
                        break;
                    }
                    move = null;
                }
                if (move == null) {
                    move = Game.createPassMove(context);
                    if (context.active()) {
                        context.state().setStalemated(mover, true);
                    }
                }
                else if (context.active()) {
                    context.state().setStalemated(mover, false);
                }
            }
            if (move == null) {
                System.err.println("NoRepetitionPlayout.playout(): No move found.");
                break;
            }
            game.apply(context, move);
        }
        return trial;
    }
    
    @Override
    public boolean callsGameMoves() {
        return false;
    }
}
