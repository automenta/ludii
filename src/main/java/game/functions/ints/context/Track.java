// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.context;

import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

public final class Track extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public int eval(final Context context) {
        return context.track();
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
        return "(track)";
    }
}
