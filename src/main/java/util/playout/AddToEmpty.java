// 
// Decompiled by Procyon v0.5.36
// 

package util.playout;

import collections.FVector;
import game.Game;
import game.rules.phase.Phase;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.state.MoveAgain;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import util.*;
import util.action.Action;
import util.action.move.ActionAdd;
import util.action.others.ActionSwap;
import util.state.State;

import java.util.List;
import java.util.Random;

public class AddToEmpty extends Playout
{
    private Move[][] moveCache;
    private final SiteType type;
    
    public AddToEmpty(final SiteType type) {
        this.moveCache = null;
        this.type = type;
    }
    
    @Override
    public Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random) {
        final Game game = context.game();
        if (this.moveCache == null) {
            this.moveCache = new Move[game.players().count() + 1][game.board().topology().numSites(this.type)];
        }
        final Sites sites = new Sites(context.state().containerStates()[0].emptyRegion(this.type).sites());
        final Phase startPhase = game.rules().phases()[context.state().currentPhase(context.state().mover())];
        int numActionsApplied = 0;
        int lastTo = -1;
        double probSwap = 0.0;
        Trial trial;
        for (trial = context.trial(); !trial.over() && (maxNumPlayoutActions < 0 || maxNumPlayoutActions > numActionsApplied); ++numActionsApplied) {
            final int remaining = sites.count();
            final int mover = context.state().mover();
            final Phase currPhase = game.rules().phases()[context.state().currentPhase(mover)];
            if (currPhase != startPhase) {
                return game.playout(context, ais, thinkingTime, featureSets, weights, maxNumBiasedActions - numActionsApplied, maxNumPlayoutActions - numActionsApplied, autoPlayThreshold, random);
            }
            if (remaining < 1) {
                if (context.active()) {
                    context.state().setStalemated(mover, true);
                }
                game.apply(context, Game.createPassMove(context));
            }
            else {
                if (context.active()) {
                    context.state().setStalemated(mover, false);
                }
                final boolean canSwap = context.game().usesSwapRule() && trial.moveNumber() == game.players().count() - 1;
                if (canSwap) {
                    probSwap = 1.0 / (remaining + 1);
                }
                AI ai = null;
                if (ais != null) {
                    ai = ais.get(context.state().playerToAgent(mover));
                }
                Move move;
                if (ai != null) {
                    move = ai.selectAction(context.game(), new Context(context), thinkingTime, -1, -1);
                    if (!move.isSwap()) {
                        sites.remove(move.from());
                    }
                }
                else if (featureSets != null && (maxNumBiasedActions < 0 || maxNumBiasedActions > numActionsApplied)) {
                    FeatureSetInterface featureSet;
                    FVector weightVector;
                    if (featureSets.length == 1) {
                        featureSet = featureSets[0];
                        weightVector = weights[0];
                    }
                    else {
                        featureSet = featureSets[mover];
                        weightVector = weights[mover];
                    }
                    final int[] emptySites = sites.sites();
                    final float[] logits = canSwap ? new float[remaining + 1] : new float[remaining];
                    final State state = context.state();
                    int autoPlayIdx = -1;
                    if (autoPlayThreshold >= 0.0) {
                        for (int i = 0; i < remaining; ++i) {
                            logits[i] = featureSet.computeLogitFastReturn(state, -1, lastTo, -1, emptySites[i], autoPlayThreshold, weightVector, mover, true);
                            if (logits[i] >= autoPlayThreshold) {
                                autoPlayIdx = i;
                                break;
                            }
                        }
                    }
                    else {
                        for (int i = 0; i < remaining; ++i) {
                            final TIntArrayList sparseFeatureVector = featureSet.getActiveFeatureIndices(state, -1, lastTo, -1, emptySites[i], mover, true);
                            logits[i] = weightVector.dotSparse(sparseFeatureVector);
                        }
                    }
                    int n;
                    if (autoPlayIdx >= 0) {
                        n = autoPlayIdx;
                    }
                    else {
                        final FVector distribution = FVector.wrap(logits);
                        distribution.softmax();
                        n = distribution.sampleFromDistribution();
                    }
                    if (n >= remaining) {
                        final Moves replay = new MoveAgain();
                        final ActionSwap actionSwap = new ActionSwap(1, 2);
                        actionSwap.setDecision(true);
                        move = new Move(actionSwap);
                        move.setDecision(true);
                        move.then().add(replay);
                        assert canSwap;
                        probSwap = 0.0;
                    }
                    else {
                        final Move[] playerMoveCache = this.moveCache[mover];
                        final int nthEmpty = lastTo = emptySites[n];
                        if (playerMoveCache[nthEmpty] == null) {
                            final Action actionAdd = new ActionAdd(this.type, emptySites[n], mover, 1, -1, -1, null, null, null);
                            actionAdd.setDecision(true);
                            move = new Move(actionAdd);
                            move.setFromNonDecision(nthEmpty);
                            move.setToNonDecision(nthEmpty);
                            move.setMover(mover);
                            assert currPhase.play().moves().then() == null;
                            playerMoveCache[nthEmpty] = move;
                        }
                        else {
                            move = playerMoveCache[nthEmpty];
                        }
                        sites.removeNth(n);
                    }
                }
                else {
                    final int n2 = random.nextInt(remaining);
                    final int site = sites.nthValue(n2);
                    final Move[] playerMoveCache2 = this.moveCache[mover];
                    lastTo = site;
                    if (playerMoveCache2[site] == null) {
                        final Action actionAdd2 = new ActionAdd(this.type, site, mover, 1, -1, -1, null, null, null);
                        actionAdd2.setDecision(true);
                        move = new Move(actionAdd2);
                        move.setFromNonDecision(site);
                        move.setToNonDecision(site);
                        move.setMover(mover);
                        assert currPhase.play().moves().then() == null;
                        playerMoveCache2[site] = move;
                    }
                    else {
                        move = playerMoveCache2[site];
                    }
                    sites.removeNth(n2);
                }
                game.apply(context, move);
            }
        }
        if (random.nextDouble() < probSwap) {
            assert game.players().count() == 2;
            context.state().swapPlayerOrder(1, 2);
        }
        return trial;
    }
    
    @Override
    public boolean callsGameMoves() {
        return false;
    }
}
