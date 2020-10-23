// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.math;

import annotations.Anon;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import util.Context;

public final class Not extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final BooleanFunction a;
    private Boolean precomputedBoolean;
    
    public Not(@Anon final BooleanFunction a) {
        this.a = a;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.precomputedBoolean != null) {
            return this.precomputedBoolean;
        }
        return !this.a.eval(context);
    }
    
    public BooleanFunction a() {
        return this.a;
    }
    
    @Override
    public String toString() {
        return "Not(" + this.a.toString() + ")";
    }
    
    @Override
    public boolean isStatic() {
        return this.a.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.a.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.a.preprocess(game);
        if (this.isStatic()) {
            this.precomputedBoolean = this.eval(new Context(game, null));
        }
    }
    
    @Override
    public boolean autoFails() {
        return this.a.autoSucceeds();
    }
    
    @Override
    public boolean autoSucceeds() {
        return this.a.autoFails();
    }
}
