// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.Game;

public class ThinkingThread extends Thread
{
    protected final ThinkingThreadRunnable runnable;
    
    public static ThinkingThread construct(final AI ai, final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth, final double minSeconds, final Runnable postThinking) {
        final ThinkingThreadRunnable runnable = new ThinkingThreadRunnable(ai, game, context, maxSeconds, maxIterations, maxDepth, minSeconds, postThinking);
        return new ThinkingThread(runnable);
    }
    
    protected ThinkingThread(final ThinkingThreadRunnable runnable) {
        super(runnable);
        this.runnable = runnable;
    }
    
    public AI ai() {
        return this.runnable.ai;
    }
    
    public Move move() {
        return this.runnable.chosenMove;
    }
    
    public AI interruptAI() {
        this.runnable.postThinking = null;
        this.runnable.ai.setWantsInterrupt(true);
        return this.runnable.ai;
    }
    
    private static class ThinkingThreadRunnable implements Runnable
    {
        protected final AI ai;
        protected final Game game;
        protected final Context context;
        protected final double maxSeconds;
        protected final int maxIterations;
        protected final int maxDepth;
        protected final double minSeconds;
        protected Runnable postThinking;
        protected Move chosenMove;
        
        public ThinkingThreadRunnable(final AI ai, final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth, final double minSeconds, final Runnable postThinking) {
            this.chosenMove = null;
            this.ai = ai;
            this.game = game;
            this.context = context;
            this.maxSeconds = maxSeconds;
            this.maxIterations = maxIterations;
            this.maxDepth = maxDepth;
            this.minSeconds = minSeconds;
            this.postThinking = postThinking;
        }
        
        @Override
        public void run() {
            final long startTime = System.currentTimeMillis();
            this.chosenMove = this.ai.selectAction(this.game, new Context(this.context), this.maxSeconds, this.maxIterations, this.maxDepth);
            if (System.currentTimeMillis() < startTime + 1000.0 * this.minSeconds) {
                try {
                    Thread.sleep((long)(startTime + 1000.0 * this.minSeconds) - System.currentTimeMillis());
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (this.postThinking != null) {
                this.postThinking.run();
            }
        }
    }
}
