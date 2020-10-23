// 
// Decompiled by Procyon v0.5.36
// 

package util.model;

import collections.FVector;
import collections.FastArrayList;
import game.Game;
import game.rules.play.moves.Moves;
import gnu.trove.list.array.TIntArrayList;
import util.*;
import util.action.Action;
import util.action.others.ActionPass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class SimultaneousMove extends Model
{
    protected transient boolean ready;
    protected transient boolean running;
    protected transient Move[] movesPerPlayer;
    protected transient ThinkingThread[] currentThinkingThreads;
    protected transient AI[] lastStepAIs;
    protected transient Move[] lastStepMoves;
    protected transient AgentMoveCallback preAgentMoveCallback;
    protected transient AgentMoveCallback postAgentMoveCallback;
    
    public SimultaneousMove() {
        this.ready = true;
        this.running = false;
        this.movesPerPlayer = null;
        this.currentThinkingThreads = null;
    }
    
    @Override
    public Move applyHumanMove(final Context context, final Move move, final int player) {
        if (this.currentThinkingThreads[player] != null) {
            return null;
        }
        if (this.movesPerPlayer[player] == null) {
            this.addMoveForPlayer(context, move, player);
            return move;
        }
        return null;
    }
    
    @Override
    public Model copy() {
        return new SimultaneousMove();
    }
    
    @Override
    public boolean expectsHumanInput() {
        if (!this.ready && this.running) {
            for (int p = 1; p < this.currentThinkingThreads.length; ++p) {
                if (this.currentThinkingThreads[p] == null && this.movesPerPlayer[p] == null) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    @Override
    public synchronized void interruptAIs() {
        if (!this.ready) {
            final List<AI> interruptedAIs = new ArrayList<>();
            boolean stillHaveLiveAIs = false;
            for (int p = 1; p < this.currentThinkingThreads.length; ++p) {
                if (this.currentThinkingThreads[p] != null) {
                    final AI ai = this.currentThinkingThreads[p].interruptAI();
                    if (ai != null) {
                        interruptedAIs.add(ai);
                    }
                }
            }
            stillHaveLiveAIs = true;
            while (stillHaveLiveAIs) {
                stillHaveLiveAIs = false;
                for (int p = 1; p < this.currentThinkingThreads.length; ++p) {
                    if (this.currentThinkingThreads[p] != null) {
                        if (this.currentThinkingThreads[p].isAlive()) {
                            stillHaveLiveAIs = true;
                            break;
                        }
                        this.currentThinkingThreads[p] = null;
                    }
                }
                if (stillHaveLiveAIs) {
                    try {
                        Thread.sleep(15L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (AI ai : interruptedAIs) {
                ai.setWantsInterrupt(false);
            }
            this.lastStepAIs = new AI[this.lastStepAIs.length];
            this.lastStepMoves = new Move[this.lastStepMoves.length];
            this.ready = true;
            this.running = false;
        }
    }
    
    @Override
    public List<AI> getLastStepAIs() {
        if (!this.ready) {
            return null;
        }
        return Arrays.asList(this.lastStepAIs);
    }
    
    @Override
    public List<Move> getLastStepMoves() {
        if (!this.ready) {
            return null;
        }
        return Arrays.asList(this.lastStepMoves);
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
        if (!this.ready) {
            final FastArrayList<Move> legalMoves = context.game().moves(context).moves();
            for (int p = 1; p < this.currentThinkingThreads.length; ++p) {
                if (this.currentThinkingThreads[p] == null && this.movesPerPlayer[p] == null) {
                    final FastArrayList<Move> playerMoves = new FastArrayList<>(legalMoves.size());
                    for (final Move move : legalMoves) {
                        if (move.mover() == p) {
                            playerMoves.add(move);
                        }
                    }
                    if (playerMoves.isEmpty()) {
                        final ActionPass actionPass = new ActionPass();
                        actionPass.setDecision(true);
                        final Move passMove = new Move(actionPass);
                        passMove.setMover(p);
                        if (inPreAgentMoveCallback != null) {
                            inPreAgentMoveCallback.call(passMove);
                        }
                        this.applyHumanMove(context, passMove, p);
                        if (inPostAgentMoveCallback != null) {
                            inPostAgentMoveCallback.call(passMove);
                        }
                        return;
                    }
                    final int r = ThreadLocalRandom.current().nextInt(playerMoves.size());
                    Move move = playerMoves.get(r);
                    if (inPreAgentMoveCallback != null) {
                        inPreAgentMoveCallback.call(move);
                    }
                    this.applyHumanMove(context, move, p);
                    if (inPostAgentMoveCallback != null) {
                        inPostAgentMoveCallback.call(move);
                    }
                }
            }
        }
    }
    
    @Override
    public synchronized void startNewStep(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final boolean block, final boolean forceThreaded, final boolean forceNotThreaded, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        if (!this.ready) {
            return;
        }
        this.ready = false;
        this.preAgentMoveCallback = inPreAgentMoveCallback;
        this.postAgentMoveCallback = inPostAgentMoveCallback;
        final int numPlayers = context.game().players().count();
        this.movesPerPlayer = new Move[numPlayers + 1];
        this.lastStepAIs = new AI[numPlayers + 1];
        this.lastStepMoves = new Move[numPlayers + 1];
        this.currentThinkingThreads = new ThinkingThread[numPlayers + 1];
        final FastArrayList<Move> legalMoves = context.game().moves(context).moves();
        if (block && forceNotThreaded) {
            for (int p = 1; p <= numPlayers; ++p) {
                if (context.active(p)) {
                    this.lastStepAIs[p] = ais.get(p);
                    final Move move = ais.get(p).selectAction(context.game(), new Context(context), maxSeconds[p], maxIterations, maxSearchDepth);
                    this.movesPerPlayer[p] = move;
                    this.lastStepMoves[p] = move;
                }
            }
            this.applyCombinedMove(context);
        }
        else {
            for (int p = 1; p <= numPlayers; ++p) {
                AI agent;
                if (ais == null || p >= ais.size()) {
                    agent = null;
                }
                else {
                    agent = ais.get(p);
                }
                if (ais != null) {
                    this.lastStepAIs[p] = agent;
                }
                if (context.active(p) && agent != null) {
                    (this.currentThinkingThreads[p] = ThinkingThread.construct(agent, context.game(), new Context(context), maxSeconds[p], maxIterations, maxSearchDepth, minSeconds, this.createPostThinking(context, block, p))).setDaemon(true);
                    this.currentThinkingThreads[p].start();
                }
                else if (context.active(p)) {
                    boolean humanHasMoves = false;
                    for (final Move move2 : legalMoves) {
                        if (move2.mover() == p) {
                            humanHasMoves = true;
                            break;
                        }
                    }
                    if (!humanHasMoves) {
                        final ActionPass actionPass = new ActionPass();
                        actionPass.setDecision(true);
                        final Move passMove = new Move(actionPass);
                        passMove.setMover(p);
                        if (inPreAgentMoveCallback != null) {
                            inPreAgentMoveCallback.call(passMove);
                        }
                        this.addMoveForPlayer(context, passMove, p);
                        if (inPostAgentMoveCallback != null) {
                            inPostAgentMoveCallback.call(passMove);
                        }
                    }
                }
            }
            if (block) {
                for (boolean threadsAlive = true; threadsAlive; threadsAlive = true) {
                    threadsAlive = false;
                    for (int p2 = 1; p2 < this.currentThinkingThreads.length; ++p2) {
                        if (this.currentThinkingThreads[p2] != null && this.currentThinkingThreads[p2].isAlive()) {
                            break;
                        }
                    }
                }
                for (int p2 = 1; p2 < this.currentThinkingThreads.length; ++p2) {
                    if (this.currentThinkingThreads[p2] != null) {
                        this.movesPerPlayer[p2] = this.currentThinkingThreads[p2].move();
                        this.currentThinkingThreads[p2] = null;
                        this.lastStepMoves[p2] = this.movesPerPlayer[p2];
                    }
                }
                this.applyCombinedMove(context);
            }
            else {
                this.running = true;
            }
        }
    }
    
    @Override
    public void unpauseAgents(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback) {
        final int numPlayers = context.game().players().count();
        this.currentThinkingThreads = new ThinkingThread[numPlayers + 1];
        this.preAgentMoveCallback = inPreAgentMoveCallback;
        this.postAgentMoveCallback = inPostAgentMoveCallback;
        for (int p = 1; p <= numPlayers; ++p) {
            AI agent;
            if (ais == null || p >= ais.size()) {
                agent = null;
            }
            else {
                agent = ais.get(p);
            }
            if (ais != null) {
                this.lastStepAIs[p] = agent;
            }
            if (context.active(p) && agent != null && this.movesPerPlayer[p] == null) {
                (this.currentThinkingThreads[p] = ThinkingThread.construct(agent, context.game(), new Context(context), maxSeconds[p], maxIterations, maxSearchDepth, minSeconds, this.createPostThinking(context, false, p))).setDaemon(true);
                this.currentThinkingThreads[p].start();
            }
        }
    }
    
    @Override
    public List<AI> getLiveAIs() {
        final List<AI> ais = new ArrayList<>(this.currentThinkingThreads.length);
        for (final ThinkingThread thinkingThread : this.currentThinkingThreads) {
            if (thinkingThread != null) {
                ais.add(thinkingThread.ai());
            }
        }
        return ais;
    }
    
    @Override
    public boolean verifyMoveLegal(final Context context, final Move move) {
        boolean validMove = false;
        final FastArrayList<Move> legal = new FastArrayList<>(context.game().moves(context).moves());
        final int mover = move.mover();
        boolean noLegalMoveForMover = true;
        for (final Move m : legal) {
            if (m.getAllActions(context).equals(move.getAllActions(context))) {
                validMove = true;
                break;
            }
            if (m.mover() != mover) {
                continue;
            }
            noLegalMoveForMover = false;
        }
        if (noLegalMoveForMover && move.isPass()) {
            validMove = true;
        }
        return validMove;
    }
    
    void addMoveForPlayer(final Context context, final Move move, final int p) {
        this.movesPerPlayer[p] = move;
        this.lastStepMoves[p] = move;
        this.maybeApplyCombinedMove(context);
    }
    
    private synchronized void maybeApplyCombinedMove(final Context context) {
        if (!this.ready) {
            for (int numPlayers = context.game().players().count(), i = 1; i <= numPlayers; ++i) {
                if (context.active(i) && this.movesPerPlayer[i] == null) {
                    return;
                }
            }
            this.applyCombinedMove(context);
        }
    }
    
    private void applyCombinedMove(final Context context) {
        final List<Action> actions = new ArrayList<>();
        final List<Moves> topLevelCons = new ArrayList<>();
        int numSubmoves = 0;
        for (int p = 1; p < this.movesPerPlayer.length; ++p) {
            final Move move = this.movesPerPlayer[p];
            if (move != null) {
                final Move moveToAdd = new Move(move.actions());
                actions.add(moveToAdd);
                ++numSubmoves;
                if (move.then() != null) {
                    for (int i = 0; i < move.then().size(); ++i) {
                        if (move.then().get(i).applyAfterAllMoves()) {
                            topLevelCons.add(move.then().get(i));
                        }
                        else {
                            moveToAdd.then().add(move.then().get(i));
                        }
                    }
                }
            }
        }
        final Move combinedMove = new Move(actions);
        combinedMove.setMover(this.movesPerPlayer.length);
        combinedMove.then().addAll(topLevelCons);
        context.game().apply(context, combinedMove);
        context.trial().setNumSubmovesPlayed(context.trial().numSubmovesPlayed() + numSubmoves);
        Arrays.fill(this.movesPerPlayer, null);
        this.ready = true;
        this.running = false;
    }
    
    private Runnable createPostThinking(final Context context, final boolean block, final int p) {
        if (block) {
            return null;
        }
        return () -> {
            Move move = this.currentThinkingThreads[p].move();
            this.currentThinkingThreads[p] = null;
            if (this.preAgentMoveCallback != null) {
                this.preAgentMoveCallback.call(move);
            }
            this.addMoveForPlayer(context, move, p);
            if (this.postAgentMoveCallback != null) {
                this.postAgentMoveCallback.call(move);
            }
        };
    }
    
    @Override
    public Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random) {
        final Game game = context.game();
        final int numPlayers = game.players().count();
        for (int numActionsApplied = 0; !context.trial().over() && (maxNumPlayoutActions < 0 || maxNumPlayoutActions > numActionsApplied); ++numActionsApplied) {
            final Move[] movesPerPlayerPlayout = new Move[numPlayers + 1];
            final Moves legal = game.moves(context);
            final List<FastArrayList<Move>> legalPerPlayer = new ArrayList<>(numPlayers + 1);
            legalPerPlayer.add(null);
            for (int p = 1; p <= numPlayers; ++p) {
                legalPerPlayer.add(new FastArrayList<>());
            }
            for (final Move move : legal.moves()) {
                legalPerPlayer.get(move.mover()).add(move);
            }
            for (int p = 1; p <= numPlayers; ++p) {
                if (context.active(p)) {
                    AI ai;
                    if (ais != null) {
                        ai = ais.get(p);
                    }
                    else {
                        ai = null;
                    }
                    if (ai != null) {
                        movesPerPlayerPlayout[p] = ai.selectAction(game, new Context(context), thinkingTime, -1, -1);
                    }
                    else {
                        final FastArrayList<Move> playerMoves = legalPerPlayer.get(p);
                        if (playerMoves.isEmpty()) {
                            final ActionPass actionPass = new ActionPass();
                            actionPass.setDecision(true);
                            final Move passMove = new Move(actionPass);
                            passMove.setMover(p);
                            playerMoves.add(passMove);
                        }
                        if (featureSets != null && (maxNumBiasedActions < 0 || maxNumBiasedActions > numActionsApplied)) {
                            FeatureSetInterface featureSet;
                            FVector weightVector;
                            if (featureSets.length == 1) {
                                featureSet = featureSets[0];
                                weightVector = weights[0];
                            }
                            else {
                                featureSet = featureSets[p];
                                weightVector = weights[p];
                            }
                            final List<TIntArrayList> sparseFeatureVectors = featureSet.computeSparseFeatureVectors(context, playerMoves, true);
                            final float[] logits = new float[sparseFeatureVectors.size()];
                            for (int i = 0; i < sparseFeatureVectors.size(); ++i) {
                                logits[i] = weightVector.dotSparse(sparseFeatureVectors.get(i));
                            }
                            final FVector distribution = FVector.wrap(logits);
                            distribution.softmax();
                            final int n = distribution.sampleFromDistribution();
                            movesPerPlayerPlayout[p] = playerMoves.get(n);
                        }
                        else {
                            final int r = random.nextInt(playerMoves.size());
                            movesPerPlayerPlayout[p] = playerMoves.get(r);
                        }
                    }
                }
            }
            final List<Action> actions = new ArrayList<>();
            final List<Moves> topLevelCons = new ArrayList<>();
            for (int p2 = 1; p2 < movesPerPlayerPlayout.length; ++p2) {
                final Move move2 = movesPerPlayerPlayout[p2];
                if (move2 != null) {
                    final Move moveToAdd = new Move(move2.actions());
                    actions.add(moveToAdd);
                    if (move2.then() != null) {
                        for (int j = 0; j < move2.then().size(); ++j) {
                            if (move2.then().get(j).applyAfterAllMoves()) {
                                topLevelCons.add(move2.then().get(j));
                            }
                            else {
                                moveToAdd.then().add(move2.then().get(j));
                            }
                        }
                    }
                }
            }
            final Move combinedMove = new Move(actions);
            combinedMove.setMover(movesPerPlayerPlayout.length);
            combinedMove.then().addAll(topLevelCons);
            game.apply(context, combinedMove);
        }
        return context.trial();
    }
    
    @Override
    public boolean callsGameMoves() {
        return true;
    }
    
    @Override
    public Move[] movesPerPlayer() {
        return this.movesPerPlayer;
    }
}
