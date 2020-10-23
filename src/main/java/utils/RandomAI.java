// 
// Decompiled by Procyon v0.5.36
// 

package utils;

import game.Game;
import main.collections.FastArrayList;
import util.AI;
import util.Context;
import util.Move;

import java.util.concurrent.ThreadLocalRandom;

public class RandomAI extends AI
{
    protected int player;
    protected Move lastReturnedMove;
    
    public RandomAI() {
        this.player = -1;
        this.lastReturnedMove = null;
        this.friendlyName = "Random";
    }
    
    @Override
    public Move selectAction(final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth) {
        final FastArrayList<Move> legalMoves = game.moves(context).moves();
        final int r = ThreadLocalRandom.current().nextInt(legalMoves.size());
        final Move move = legalMoves.get(r);
        return this.lastReturnedMove = move;
    }
    
    public Move lastReturnedMove() {
        return this.lastReturnedMove;
    }
    
    @Override
    public void initAI(final Game game, final int playerID) {
        this.player = playerID;
        this.lastReturnedMove = null;
    }
}
