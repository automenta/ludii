// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.math;

import annotations.Alias;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import util.Context;

@Alias(alias = "^")
public final class Pow extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction a;
    private final IntFunction b;
    private int precomputedValue;
    
    public Pow(final IntFunction a, final IntFunction b) {
        this.precomputedValue = -1;
        this.a = a;
        this.b = b;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        return (int)Math.pow(this.a.eval(context), this.b.eval(context));
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
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
}
