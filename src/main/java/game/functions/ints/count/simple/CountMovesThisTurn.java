// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.simple;

import annotations.Hide;
import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

@Hide
public final class CountMovesThisTurn extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public int eval(final Context context) {
        return context.state().numTurnSamePlayer();
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "MovesThisTurn()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
