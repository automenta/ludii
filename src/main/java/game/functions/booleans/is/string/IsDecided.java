// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.string;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import util.Context;

@Hide
public final class IsDecided extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final String decision;
    
    public IsDecided(final String decision) {
        this.decision = decision;
    }
    
    @Override
    public boolean eval(final Context context) {
        return context.state().isDecided().equals(this.decision);
    }
    
    @Override
    public String toString() {
        return "IsDecided()";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 2147483648L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
