// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.context;

import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

public final class Level extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public int eval(final Context context) {
        return context.level();
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 16L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        return "Level()";
    }
}
