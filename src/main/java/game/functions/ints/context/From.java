// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.context;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.types.play.WhenType;
import util.Context;

public final class From extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    final WhenType at;
    
    public From(@Opt @Name final WhenType at) {
        this.at = at;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.at == WhenType.StartOfTurn) {
            return context.fromStartOfTurn();
        }
        return context.from();
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        return "From()";
    }
}
