// 
// Decompiled by Procyon v0.5.36
// 

package util.model;

import game.Game;
import game.match.Subgame;
import game.rules.play.moves.Moves;
import collections.FVector;
import util.*;

import java.util.List;
import java.util.Random;

public class MatchModel extends Model
{
    protected transient Model currentInstanceModel;
    
    public MatchModel() {
        this.currentInstanceModel = null;
    }
    
    @Override
    public Move applyHumanMove(final Context context, final Move move, final int player) {
        return this.currentInstanceModel.applyHumanMove(context, move, player);
    }
    
    @Override
    public Model copy() {
        return new MatchModel();
    }
    
    @Override
    public boolean expectsHumanInput() {
        return this.currentInstanceModel != null && this.currentInstanceModel.expectsHumanInput();
    }
    
    @Override
    public List<AI> getLastStepAIs() {
        return this.currentInstanceModel.getLastStepAIs();
    }
    
    @Override
    public List<Move> getLastStepMoves() {
        return this.currentInstanceModel.getLastStepMoves();
    }
    
    @Override
    public synchronized void interruptAIs() {
        if (this.currentInstanceModel != null) {
            this.currentInstanceModel.interruptAIs();
        }
    }
    
    @Override
    public boolean isReady() {
        return this.currentInstanceModel == null || this.currentInstanceModel.isReady();
    }
    
    @Override
    public boolean isRunning() {
        return this.currentInstanceModel != null && this.currentInstanceModel.isRunning();
    }
    
    @Override
    public void randomStep(final Context context, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        this.currentInstanceModel.randomStep(context, inPreAgentMoveCallback, inPostAgentMoveCallback);
    }
    
    public void resetCurrentInstanceModel() {
        this.currentInstanceModel = null;
    }
    
    @Override
    public boolean verifyMoveLegal(final Context context, final Move move) {
        return this.currentInstanceModel.verifyMoveLegal(context, move);
    }
    
    @Override
    public synchronized void startNewStep(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final boolean block, final boolean forceThreaded, final boolean forceNotThreaded, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        (this.currentInstanceModel = context.subcontext().model()).startNewStep(context, ais, maxSeconds, maxIterations, maxSearchDepth, minSeconds, block, forceThreaded, forceNotThreaded, inPreAgentMoveCallback, inPostAgentMoveCallback);
    }
    
    @Override
    public void unpauseAgents(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        this.currentInstanceModel.unpauseAgents(context, ais, maxSeconds, maxIterations, maxSearchDepth, minSeconds, inPreAgentMoveCallback, inPostAgentMoveCallback);
    }
    
    @Override
    public List<AI> getLiveAIs() {
        return this.currentInstanceModel.getLiveAIs();
    }
    
    @Override
    public Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random) {
        final Game match = context.game();
        final Trial matchTrial = context.trial();
        int numStartMoves = 0;

        Trial instanceEndTrial = null;
        for (int numActionsApplied = 0; !matchTrial.over() && (maxNumPlayoutActions < 0 || maxNumPlayoutActions > numActionsApplied); numActionsApplied += instanceEndTrial.numMoves() - numStartMoves) {
            final Subgame instance = match.instances()[context.currentSubgameIdx()];
            final Game instanceGame = instance.getGame();
            final Context subcontext = context.subcontext();
            final Trial subtrial = subcontext.trial();
            numStartMoves = subtrial.numMoves();
            if (context.trial().auxilTrialData() != null) {
                if (context.trial().auxilTrialData().legalMovesHistory() != null) {
                    subtrial.storeLegalMovesHistory();
                }
                if (context.trial().auxilTrialData().legalMovesHistorySizes() != null) {
                    subtrial.storeLegalMovesHistorySizes();
                }
            }
            instanceEndTrial = instanceGame.playout(subcontext, ais, thinkingTime, featureSets, weights, maxNumBiasedActions, maxNumPlayoutActions - numActionsApplied, autoPlayThreshold, random);
            final List<Move> subtrialMoves = subtrial.moves();
            final int numMovesAfterPlayout = subtrialMoves.size();
            for (int numMovesToAppend = numMovesAfterPlayout - numStartMoves, i = 0; i < numMovesToAppend; ++i) {
                context.trial().moves().add(subtrialMoves.get(subtrialMoves.size() - numMovesToAppend + i));
            }
            if (subcontext.trial().over()) {
                final Moves legalMatchMoves = context.game().moves(context);
                assert legalMatchMoves.moves().size() == 1;
                assert legalMatchMoves.moves().get(0).containsNextInstance();
                context.game().apply(context, legalMatchMoves.moves().get(0));
            }
            if (context.trial().auxilTrialData() != null) {
                context.trial().auxilTrialData().updateFromSubtrial(subtrial);
                if (context.trial().auxilTrialData().legalMovesHistorySizes() != null) {
                    context.trial().auxilTrialData().legalMovesHistorySizes().add(1);
                }
            }
        }
        return matchTrial;
    }
    
    @Override
    public boolean callsGameMoves() {
        return true;
    }
}
