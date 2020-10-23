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
public final class IsOdd extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction value;
    private Boolean precomputedBoolean;
    
    public IsOdd(final IntFunction value) {
        this.value = value;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.precomputedBoolean != null) {
            return this.precomputedBoolean;
        }
        return (this.value.eval(context) & 0x1) == 0x1;
    }
    
    @Override
    public boolean isStatic() {
        return this.value.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.value.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.value.preprocess(game);
        if (this.isStatic()) {
            this.precomputedBoolean = this.eval(new Context(game, null));
        }
    }
}
