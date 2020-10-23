// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.all;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import util.Context;

@Hide
public final class AllPassed extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean eval(final Context context) {
        return context.trial().moveNumber() >= context.game().players().count() && context.allPass();
    }
    
    @Override
    public String toString() {
        return "AllPass()";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 4096L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
