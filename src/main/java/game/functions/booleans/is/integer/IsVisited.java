// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.integer;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import util.Context;

@Hide
public final class IsVisited extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction siteId;
    
    public IsVisited(final IntFunction site) {
        this.siteId = site;
    }
    
    @Override
    public boolean eval(final Context context) {
        return context.state().isVisited(this.siteId.eval(context));
    }
    
    @Override
    public String toString() {
        return "Visited(" + this.siteId + ")";
    }
    
    @Override
    public boolean isStatic() {
        return this.siteId.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 1073742336L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.siteId.preprocess(game);
    }
}
