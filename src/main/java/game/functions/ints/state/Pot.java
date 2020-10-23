// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.state;

import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

public final class Pot extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public final int eval(final Context context) {
        return context.state().pot();
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
}
