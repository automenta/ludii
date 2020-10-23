// 
// Decompiled by Procyon v0.5.36
// 

package util.playout;

import game.Game;
import game.functions.booleans.BooleanFunction;
import game.rules.phase.Phase;
import game.rules.play.Play;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Pass;
import game.rules.play.moves.nonDecision.effect.requirement.Do;
import game.rules.play.moves.nonDecision.effect.state.MoveAgain;
import game.rules.play.moves.nonDecision.operators.logical.If;
import game.rules.play.moves.nonDecision.operators.logical.Or;
import gnu.trove.list.array.TIntArrayList;
import collections.FVector;
import collections.FastArrayList;
import util.*;
import util.action.others.ActionSwap;

import java.util.List;
import java.util.Random;

public class FilterPlayout extends Playout
{
    @Override
    public Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random) {
        final Game game = context.game();
        final Phase startPhase = game.rules().phases()[context.state().currentPhase(context.state().mover())];
        final Play playRules = startPhase.play();
        Do doRule;
        If ifRule;
        Pass passRule;
        Or orRule;
        if (playRules.moves() instanceof Do) {
            doRule = (Do)playRules.moves();
            ifRule = null;
            passRule = null;
            orRule = null;
        }
        else if (playRules.moves() instanceof If) {
            ifRule = (If)playRules.moves();
            passRule = null;
            orRule = null;
            if (!(ifRule.elseList() instanceof Do)) {
                throw new UnsupportedOperationException("Cannot use FilterPlayout for phase with else-rules of type: " + ifRule.elseList().getClass());
            }
            doRule = (Do)ifRule.elseList();
        }
        else {
            if (!(playRules.moves() instanceof Or)) {
                throw new UnsupportedOperationException("Cannot use FilterPlayout for phase with play rules of type: " + playRules.moves().getClass());
            }
            orRule = (Or)playRules.moves();
            ifRule = null;
            if (orRule.list().length != 2 || !(orRule.list()[0] instanceof Do) || !(orRule.list()[1] instanceof Pass)) {
                throw new UnsupportedOperationException("Invalid Or-rules for FilterPlayout!");
            }
            doRule = (Do)orRule.list()[0];
            passRule = (Pass)orRule.list()[1];
        }
        Moves priorMoves;
        Moves mainMovesGenerator;
        if (doRule.after() == null) {
            priorMoves = null;
            mainMovesGenerator = doRule.prior();
        }
        else {
            priorMoves = doRule.prior();
            mainMovesGenerator = doRule.after();
        }
        final BooleanFunction condition = doRule.ifAfter();
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
        boolean mustCheckCondition;
        Context movesGenContext;
        int j;
        Moves priorMovesGenerated;
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
                if (ifRule != null && ifRule.cond().eval(context)) {
                    legalMoves = ifRule.list().eval(context);
                    mustCheckCondition = false;
                    movesGenContext = context;
                    if (ifRule.then() != null) {
                        for (j = 0; j < legalMoves.moves().size(); ++j) {
                            legalMoves.moves().get(j).then().add(ifRule.then().moves());
                        }
                    }
                }
                else {
                    if (priorMoves != null) {
                        movesGenContext = new Context(context);
                        priorMovesGenerated = doRule.generateAndApplyPreMoves(context, movesGenContext);
                    }
                    else {
                        movesGenContext = context;
                        priorMovesGenerated = null;
                    }
                    legalMoves = mainMovesGenerator.eval(movesGenContext);
                    mustCheckCondition = !condition.autoSucceeds();
                    if (priorMovesGenerated != null) {
                        Do.prependPreMoves(priorMovesGenerated, legalMoves, movesGenContext);
                    }
                }
                if (passRule != null) {
                    legalMoves.moves().addAll(passRule.eval(movesGenContext).moves());
                }
                if (orRule != null && orRule.then() != null) {
                    for (j = 0; j < legalMoves.moves().size(); ++j) {
                        legalMoves.moves().get(j).then().add(orRule.then().moves());
                    }
                }
                if (context.game().usesSwapRule() && trial.moveNumber() == movesGenContext.game().players().count() - 1) {
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
                    if (mustCheckCondition && (passRule == null || !move.isPass()) && !move.isSwap() && !doRule.movePassesCond(move, movesGenContext)) {
                        move = null;
                    }
                    else {
                        if (Game.satisfiesStateComparison(movesGenContext, move)) {
                            break;
                        }
                        move = null;
                    }
                }
                if (move == null) {
                    move = Game.createPassMove(context);
                    if (context.active()) {
                        context.state().setStalemated(mover, true);
                    }
                }
                else {
                    if (doRule.then() != null) {
                        move.then().add(doRule.then().moves());
                    }
                    if (ifRule != null && ifRule.then() != null) {
                        move.then().add(ifRule.then().moves());
                    }
                    if (context.active()) {
                        context.state().setStalemated(mover, false);
                    }
                }
            }
            if (move == null) {
                System.err.println("FilterPlayout.playout(): No move found.");
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
