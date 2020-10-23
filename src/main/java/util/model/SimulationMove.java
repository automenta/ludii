// 
// Decompiled by Procyon v0.5.36
// 

package util.model;

import collections.FVector;
import collections.FastArrayList;
import game.Game;
import main.Status;
import util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class SimulationMove extends Model
{
    protected transient volatile boolean ready;
    protected transient volatile boolean running;
    protected transient volatile ThinkingThread currentThinkingThread;
    protected transient AI lastStepAI;
    protected transient Move lastStepMove;
    
    public SimulationMove() {
        this.ready = true;
        this.running = false;
        this.currentThinkingThread = null;
        this.lastStepAI = null;
        this.lastStepMove = null;
    }
    
    @Override
    public Move applyHumanMove(final Context context, final Move move, final int player) {
        if (!this.ready) {
            final Move appliedMove = context.game().apply(context, move);
            context.trial().setNumSubmovesPlayed(context.trial().numSubmovesPlayed() + 1);
            this.lastStepMove = move;
            this.ready = true;
            this.running = false;
            return appliedMove;
        }
        return null;
    }
    
    @Override
    public Model copy() {
        return new AlternatingMove();
    }
    
    @Override
    public boolean expectsHumanInput() {
        return !this.ready && this.running && this.currentThinkingThread == null;
    }
    
    @Override
    public List<AI> getLastStepAIs() {
        if (!this.ready) {
            return null;
        }
        return Collections.singletonList(this.lastStepAI);
    }
    
    @Override
    public List<Move> getLastStepMoves() {
        if (!this.ready) {
            return null;
        }
        return Collections.singletonList(this.lastStepMove);
    }
    
    @Override
    public synchronized void interruptAIs() {
        if (!this.ready) {
            if (this.currentThinkingThread != null) {
                AI ai = null;
                try {
                    ai = this.currentThinkingThread.interruptAI();
                    while (this.currentThinkingThread.isAlive()) {
                        try {
                            Thread.sleep(15L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    this.currentThinkingThread = null;
                }
                catch (NullPointerException ex) {}
                if (ai != null) {
                    ai.setWantsInterrupt(false);
                }
            }
            this.lastStepAI = null;
            this.ready = true;
            this.running = false;
        }
    }
    
    @Override
    public boolean isReady() {
        return this.ready;
    }
    
    @Override
    public boolean isRunning() {
        return this.running;
    }
    
    @Override
    public void randomStep(final Context context, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        if (!this.ready && this.currentThinkingThread == null) {
            final FastArrayList<Move> legalMoves = new FastArrayList<>(context.game().moves(context).moves());
            if (!legalMoves.isEmpty()) {
                this.applyHumanMove(context, legalMoves.get(0), context.state().mover());
            }
            this.ready = true;
            this.running = false;
        }
    }
    
    @Override
    public synchronized void startNewStep(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final boolean block, final boolean forceThreaded, final boolean forceNotThreaded, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        this.ready = false;
        this.running = true;
    }
    
    @Override
    public void unpauseAgents(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        (this.currentThinkingThread = ThinkingThread.construct(ais.get(0), context.game(), new Context(context), maxSeconds[0], maxIterations, maxSearchDepth, minSeconds, () -> {
            final long startTime = System.currentTimeMillis();
            final long stopTime = (maxSeconds[0] > 0.0) ? (startTime + (long)(maxSeconds[0] * 1000.0)) : Long.MAX_VALUE;
            while (System.currentTimeMillis() < stopTime) {}
            final FastArrayList<Move> legalMoves = new FastArrayList<>(context.game().moves(context).moves());
            if (!legalMoves.isEmpty()) {
                SimulationMove.this.applyHumanMove(context, legalMoves.get(0), context.state().mover());
            }
            SimulationMove.this.ready = true;
            SimulationMove.this.running = false;
        })).setDaemon(true);
        this.currentThinkingThread.start();
    }
    
    @Override
    public List<AI> getLiveAIs() {
        final List<AI> ais = new ArrayList<>(1);
        if (this.currentThinkingThread != null) {
            ais.add(this.currentThinkingThread.ai());
        }
        return ais;
    }
    
    @Override
    public boolean verifyMoveLegal(final Context context, final Move move) {
        return true;
    }
    
    @Override
    public Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random) {
        final Game game = context.game();
        int numActionsApplied;
        Trial trial;
        FastArrayList<Move> legalMoves;
        for (numActionsApplied = 0, trial = context.trial(); !trial.over() && (maxNumPlayoutActions < 0 || maxNumPlayoutActions > numActionsApplied); ++numActionsApplied) {
            legalMoves = new FastArrayList<>(context.game().moves(context).moves());
            if (!legalMoves.isEmpty()) {
                game.apply(context, legalMoves.get(0));
            }
            else {
                context.trial().setStatus(new Status(0));
            }
        }
        return trial;
    }
    
    @Override
    public boolean callsGameMoves() {
        return true;
    }
}
