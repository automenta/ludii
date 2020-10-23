// 
// Decompiled by Procyon v0.5.36
// 

package util.model;

import util.AI;
import util.Context;
import util.Move;
import util.playout.Playout;

import java.util.Arrays;
import java.util.List;

public abstract class Model extends Playout
{
    public abstract Move applyHumanMove(final Context context, final Move move, final int player);
    
    public abstract Model copy();
    
    public abstract boolean expectsHumanInput();
    
    public abstract List<AI> getLastStepAIs();
    
    public abstract List<Move> getLastStepMoves();
    
    public abstract void interruptAIs();
    
    public abstract boolean isReady();
    
    public abstract boolean isRunning();
    
    public abstract void randomStep(final Context context, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback);
    
    public abstract boolean verifyMoveLegal(final Context context, final Move move);
    
    public void startNewStep(final Context context, final List<AI> ais, final double maxSeconds) {
        final double[] timeLimits = new double[context.game().players().count() + 1];
        Arrays.fill(timeLimits, maxSeconds);
        this.startNewStep(context, ais, timeLimits);
    }
    
    public void startNewStep(final Context context, final List<AI> ais, final double[] maxSeconds) {
        this.startNewStep(context, ais, maxSeconds, -1, -1, 0.0);
    }
    
    public void startNewStep(final Context context, final List<AI> ais, final double maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds) {
        final double[] timeLimits = new double[context.game().players().count() + 1];
        Arrays.fill(timeLimits, maxSeconds);
        this.startNewStep(context, ais, timeLimits, maxIterations, maxSearchDepth, minSeconds);
    }
    
    public void startNewStep(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds) {
        this.startNewStep(context, ais, maxSeconds, maxIterations, maxSearchDepth, minSeconds, true, false, false, null, null);
    }
    
    public void startNewStep(final Context context, final List<AI> ais, final double maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final boolean block, final boolean forceThreaded, final boolean forceNotThreaded) {
        final double[] timeLimits = new double[context.game().players().count() + 1];
        Arrays.fill(timeLimits, maxSeconds);
        this.startNewStep(context, ais, timeLimits, maxIterations, maxSearchDepth, minSeconds, block, forceThreaded, forceNotThreaded, null, null);
    }
    
    public abstract void startNewStep(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final boolean block, final boolean forceThreaded, final boolean forceNotThreaded, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback);
    
    public abstract void unpauseAgents(final Context context, final List<AI> ais, final double[] maxSeconds, final int maxIterations, final int maxSearchDepth, final double minSeconds, final AgentMoveCallback inPreAgentMoveCallback, final AgentMoveCallback inPostAgentMoveCallback);
    
    public abstract List<AI> getLiveAIs();
    
    public Move[] movesPerPlayer() {
        return null;
    }
    
    public interface AgentMoveCallback
    {
        long call(final Move move);
    }
}
