// 
// Decompiled by Procyon v0.5.36
// 

package util.model;

import collections.FVector;
import collections.FastArrayList;
import game.Game;
import game.rules.phase.Phase;
import game.rules.play.moves.Moves;
import gnu.trove.list.array.TIntArrayList;
import util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class AlternatingMove extends Model
{
    protected transient volatile boolean ready;
    protected transient volatile boolean running;
    protected transient volatile ThinkingThread currentThinkingThread;
    protected transient AI lastStepAI;
    protected transient Move lastStepMove;
    
    public AlternatingMove() {
        this.ready = true;
        this.running = false;
        this.currentThinkingThread = null;
        this.lastStepAI = null;
        this.lastStepMove = null;
    }
    
    @Override
    public Move applyHumanMove(final Context context, final Move move, final int player) {
        if (this.currentThinkingThread != null) {
            return null;
        }
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
            final FastArrayList<Move> legalMoves = context.game().moves(context).moves();
            final int r = ThreadLocalRandom.current().nextInt(legalMoves.size());
            final Move move = legalMoves.get(r);
            if (inPreAgentMoveCallback != null) {
                final long waitMillis = inPreAgentMoveCallback.call(move);
                if (waitMillis > 0L) {
                    try {
                        Thread.sleep(waitMillis);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            final Move appliedMove = this.applyHumanMove(context, move, context.state().mover());
            this.ready = true;
            if (inPostAgentMoveCallback != null) {
                inPostAgentMoveCallback.call(appliedMove);
            }
            this.running = false;
        }
    }
    
    @Override
    public synchronized void startNewStep(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final boolean block, final boolean forceThreaded, final boolean forceNotThreaded, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        if (!this.ready) {
            return;
        }
        ThinkingThread thinkingThread;
        do {
            thinkingThread = this.currentThinkingThread;
        } while (thinkingThread != null && thinkingThread.isAlive());
        this.ready = false;
        final int mover = context.state().mover();
        AI agent;
        if (ais == null || mover >= ais.size()) {
            agent = null;
        }
        else {
            agent = ais.get(context.state().playerToAgent(mover));
        }
        this.lastStepAI = agent;
        if (block) {
            if (agent == null) {
                this.randomStep(context, inPreAgentMoveCallback, inPostAgentMoveCallback);
                return;
            }
            Move move;
            if (!forceThreaded) {
                move = agent.selectAction(context.game(), new Context(context), maxSeconds[context.state().playerToAgent(mover)], maxIterations, maxSearchDepth);
            }
            else {
                (this.currentThinkingThread = ThinkingThread.construct(agent, context.game(), new Context(context), maxSeconds[context.state().playerToAgent(mover)], maxIterations, maxSearchDepth, minSeconds, null)).setDaemon(true);
                this.currentThinkingThread.start();
                while (this.currentThinkingThread.isAlive()) {}
                move = this.currentThinkingThread.move();
                this.currentThinkingThread = null;
            }
            if (inPreAgentMoveCallback != null) {
                final long waitMillis = inPreAgentMoveCallback.call(move);
                if (waitMillis > 0L) {
                    try {
                        Thread.sleep(waitMillis);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            final Move appliedMove = context.game().apply(context, move);
            context.trial().setNumSubmovesPlayed(context.trial().numSubmovesPlayed() + 1);
            this.lastStepMove = move;
            this.ready = true;
            if (inPostAgentMoveCallback != null) {
                inPostAgentMoveCallback.call(appliedMove);
            }
            this.running = false;
        }
        else if (agent != null) {
            (this.currentThinkingThread = ThinkingThread.construct(agent, context.game(), new Context(context), maxSeconds[context.state().playerToAgent(mover)], maxIterations, maxSearchDepth, minSeconds, () -> {
                final Move move = AlternatingMove.this.currentThinkingThread.move();
                while (!AlternatingMove.this.running) {}
                if (inPreAgentMoveCallback != null) {
                    final long waitMillis = inPreAgentMoveCallback.call(move);
                    if (waitMillis > 0L) {
                        try {
                            Thread.sleep(waitMillis);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                final Move appliedMove = context.game().apply(context, move);
                context.trial().setNumSubmovesPlayed(context.trial().numSubmovesPlayed() + 1);
                AlternatingMove.this.lastStepMove = move;
                AlternatingMove.this.ready = true;
                if (inPostAgentMoveCallback != null) {
                    inPostAgentMoveCallback.call(appliedMove);
                }
                AlternatingMove.this.running = false;
                AlternatingMove.this.currentThinkingThread = null;
            })).setDaemon(true);
            this.currentThinkingThread.start();
            this.running = true;
        }
        else {
            this.running = true;

        }
    }
    
    @Override
    public void unpauseAgents(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        final int mover = context.state().mover();
        AI agent;
        if (ais == null || mover >= ais.size()) {
            agent = null;
        }
        else {
            agent = ais.get(context.state().playerToAgent(mover));
        }
        this.lastStepAI = agent;
        if (agent != null) {
            (this.currentThinkingThread = ThinkingThread.construct(agent, context.game(), new Context(context), maxSeconds[mover], maxIterations, maxSearchDepth, minSeconds, () -> {
                final Move move = AlternatingMove.this.currentThinkingThread.move();
                AlternatingMove.this.currentThinkingThread = null;
                if (inPreAgentMoveCallback != null) {
                    final long waitMillis = inPreAgentMoveCallback.call(move);
                    if (waitMillis > 0L) {
                        try {
                            Thread.sleep(waitMillis);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                final Move appliedMove = context.game().apply(context, move);
                context.trial().setNumSubmovesPlayed(context.trial().numSubmovesPlayed() + 1);
                AlternatingMove.this.lastStepMove = move;
                AlternatingMove.this.ready = true;
                if (inPostAgentMoveCallback != null) {
                    inPostAgentMoveCallback.call(appliedMove);
                }
                AlternatingMove.this.running = false;
            })).setDaemon(true);
            this.currentThinkingThread.start();
        }
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
        boolean validMove = false;
        final FastArrayList<Move> legal = new FastArrayList<>(context.game().moves(context).moves());
        for (final Move m : legal) {
            if (m.getAllActions(context).equals(move.getAllActions(context))) {
                validMove = true;
                break;
            }
        }
        if (legal.isEmpty() && move.isPass()) {
            validMove = true;
        }
        return validMove;
    }
    
    @Override
    public Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random) {
        final Game game = context.game();
        final Phase startPhase = game.rules().phases()[context.state().currentPhase(context.state().mover())];
        int numActionsApplied;
        Trial trial;
        int mover;
        Phase currPhase;
        Move move;
        AI ai;
        Moves legal;
        FeatureSetInterface featureSet;
        FVector weightVector;
        List<TIntArrayList> sparseFeatureVectors;
        float[] logits;
        int i;
        FVector distribution;
        int n;
        int r;
        for (numActionsApplied = 0, trial = context.trial(); !trial.over() && (maxNumPlayoutActions < 0 || maxNumPlayoutActions > numActionsApplied); ++numActionsApplied) {
            mover = context.state().mover();
            currPhase = game.rules().phases()[context.state().currentPhase(mover)];
            if (currPhase != startPhase && currPhase.playout() != null) {
                return currPhase.playout().playout(context, ais, thinkingTime, featureSets, weights, maxNumBiasedActions, maxNumPlayoutActions, autoPlayThreshold, random);
            }
            move = null;
            ai = null;
            if (ais != null) {
                ai = ais.get(context.state().playerToAgent(mover));
            }
            if (ai != null) {
                move = ai.selectAction(game, new Context(context), thinkingTime, -1, -1);
            }
            else {
                legal = game.moves(context);
                if (featureSets != null && (maxNumBiasedActions < 0 || maxNumBiasedActions > numActionsApplied)) {
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
                    r = random.nextInt(legal.moves().size());
                    move = legal.moves().get(r);
                }
            }
            if (move == null) {
                System.out.println("Game.playout(): No move found.");
                break;
            }
            game.apply(context, move);
        }
        return trial;
    }
    
    @Override
    public boolean callsGameMoves() {
        return true;
    }
}
