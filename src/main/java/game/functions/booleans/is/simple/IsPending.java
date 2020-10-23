// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.simple;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import util.Context;

@Hide
public final class IsPending extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean eval(final Context context) {
        return context.state().isPending();
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
