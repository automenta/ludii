// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.math;

import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import util.Context;

public final class Abs extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    protected final IntFunction value;
    private int precomputedValue;
    
    public Abs(final IntFunction value) {
        this.precomputedValue = -1;
        this.value = value;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        return Math.abs(this.value.eval(context));
    }
    
    public IntFunction value() {
        return this.value;
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
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
}
