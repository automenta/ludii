// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.graph;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.types.board.SiteType;
import util.Context;

@Hide
public final class IsLastFrom extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final SiteType type;
    
    public IsLastFrom(final SiteType type) {
        this.type = type;
    }
    
    @Override
    public boolean eval(final Context context) {
        return context.trial().lastMove().fromType() == this.type;
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
