// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.math;

import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import util.Context;

public final class Xor extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final BooleanFunction a;
    private final BooleanFunction b;
    private Boolean precomputedBoolean;
    
    public Xor(final BooleanFunction a, final BooleanFunction b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.precomputedBoolean != null) {
            return this.precomputedBoolean;
        }
        final boolean evalA = this.a.eval(context);
        final boolean evalB = this.b.eval(context);
        return (evalA && !evalB) || (!evalA && evalB);
    }
    
    @Override
    public boolean isStatic() {
        return this.a.isStatic() && this.b.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.a.gameFlags(game) | this.b.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.a.preprocess(game);
        this.b.preprocess(game);
        if (this.isStatic()) {
            this.precomputedBoolean = this.eval(new Context(game, null));
        }
    }
}
