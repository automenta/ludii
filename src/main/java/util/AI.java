// 
// Decompiled by Procyon v0.5.36
// 

package util;

import collections.FVector;
import collections.FastArrayList;
import game.Game;

import java.lang.ref.WeakReference;

public abstract class AI
{
    public String friendlyName;
    protected boolean wantsInterrupt;
    private WeakReference<Game> lastInitGame;
    private int lastInitGameStartCount;
    
    public AI() {
        this.friendlyName = "Unnamed";
        this.wantsInterrupt = false;
        this.lastInitGame = new WeakReference<>(null);
        this.lastInitGameStartCount = -1;
    }
    
    public abstract Move selectAction(final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth);
    
    public String name() {
        return this.friendlyName;
    }
    
    public void initAI(final Game game, final int playerID) {
    }
    
    public void closeAI() {
    }
    
    public boolean supportsGame(final Game game) {
        return true;
    }
    
    public double estimateValue() {
        return 0.0;
    }
    
    public String generateAnalysisReport() {
        return null;
    }
    
    public AIVisualisationData aiVisualisationData() {
        return null;
    }
    
    public void setWantsInterrupt(final boolean val) {
        this.wantsInterrupt = val;
    }
    
    public final void initIfNeeded(final Game game, final int playerID) {
        if (this.lastInitGame.get() != null && this.lastInitGame.get() == game && this.lastInitGame.get().gameStartCount() == this.lastInitGameStartCount) {
            return;
        }
        this.initAI(game, playerID);
        this.lastInitGame = new WeakReference<>(game);
        this.lastInitGameStartCount = game.gameStartCount();
    }
    
    public static class AIVisualisationData
    {
        private final FVector searchEffort;
        private final FVector valueEstimates;
        private final FastArrayList<Move> moves;
        
        public AIVisualisationData(final FVector searchEffort, final FVector valueEstimates, final FastArrayList<Move> moves) {
            this.searchEffort = searchEffort;
            this.valueEstimates = valueEstimates;
            this.moves = moves;
        }
        
        public FVector searchEffort() {
            return this.searchEffort;
        }
        
        public FVector valueEstimates() {
            return this.valueEstimates;
        }
        
        public FastArrayList<Move> moves() {
            return this.moves;
        }
    }
}
