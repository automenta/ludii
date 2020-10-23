// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.value.simple;

import annotations.Hide;
import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

@Hide
public final class ValuePending extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public int eval(final Context context) {
        if (context.state().pendingValues().size() == 1) {
            return context.state().pendingValues().iterator().next();
        }
        return 0;
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
