// 
// Decompiled by Procyon v0.5.36
// 

package utils;

import game.Game;
import util.AI;
import util.Context;
import util.Move;

public final class LudiiAI extends AI
{
    private AI currentAgent;
    
    public LudiiAI() {
        this.currentAgent = null;
        this.friendlyName = "Ludii";
    }
    
    @Override
    public Move selectAction(final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth) {
        return this.currentAgent.selectAction(game, context, maxSeconds, maxIterations, maxDepth);
    }
    
    @Override
    public void initAI(final Game game, final int playerID) {
        if (this.currentAgent != null) {
            this.currentAgent.closeAI();
        }
        this.currentAgent = AIFactory.fromMetadata(game);
        if (this.currentAgent == null) {
            if (!game.isAlternatingMoveGame()) {
                this.currentAgent = AIFactory.createAI("Flat MC");
            }
            else {
                this.currentAgent = AIFactory.createAI("UCT");
            }
        }
        this.friendlyName = "Ludii (" + this.currentAgent.friendlyName + ")";
        if (!this.currentAgent.supportsGame(game)) {
            System.err.println("Warning! Default AI (" + this.currentAgent + ") does not support game (" + game.name() + ")");
        }
        this.currentAgent.initAI(game, playerID);
    }
    
    @Override
    public boolean supportsGame(final Game game) {
        return true;
    }
    
    @Override
    public double estimateValue() {
        if (this.currentAgent != null) {
            return this.currentAgent.estimateValue();
        }
        return 0.0;
    }
    
    @Override
    public String generateAnalysisReport() {
        if (this.currentAgent != null) {
            return this.currentAgent.generateAnalysisReport();
        }
        return null;
    }
    
    @Override
    public AIVisualisationData aiVisualisationData() {
        if (this.currentAgent != null) {
            return this.currentAgent.aiVisualisationData();
        }
        return null;
    }
}
