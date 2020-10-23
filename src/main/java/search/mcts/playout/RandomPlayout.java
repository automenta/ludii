// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.playout;

import game.Game;
import util.Context;
import util.Trial;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomPlayout implements PlayoutStrategy
{
    protected int playoutTurnLimit;
    
    public RandomPlayout() {
        this.playoutTurnLimit = -1;
        this.playoutTurnLimit = -1;
    }
    
    public RandomPlayout(final int playoutTurnLimit) {
        this.playoutTurnLimit = -1;
        this.playoutTurnLimit = playoutTurnLimit;
    }
    
    @Override
    public Trial runPlayout(final Context context) {
        return context.game().playout(context, null, 1.0, null, null, 0, this.playoutTurnLimit, -1.0f, ThreadLocalRandom.current());
    }
    
    @Override
    public boolean playoutSupportsGame(final Game game) {
        return !game.isDeductionPuzzle() || this.playoutTurnLimit() > 0;
    }
    
    @Override
    public void customise(final String[] inputs) {
        for (int i = 1; i < inputs.length; ++i) {
            final String input = inputs[i];
            if (input.toLowerCase().startsWith("playoutturnlimit=")) {
                this.playoutTurnLimit = Integer.parseInt(input.substring("playoutturnlimit=".length()));
            }
        }
    }
    
    public int playoutTurnLimit() {
        return this.playoutTurnLimit;
    }
}
