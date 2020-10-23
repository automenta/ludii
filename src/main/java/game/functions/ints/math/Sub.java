// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.math;

import annotations.Alias;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import util.Context;

@Alias(alias = "-")
public final class Sub extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction valueA;
    private final IntFunction valueB;
    private int precomputedValue;
    
    public Sub(@Opt final IntFunction valueA, final IntFunction valueB) {
        this.precomputedValue = -1;
        this.valueA = ((valueA != null) ? valueA : new IntConstant(0));
        this.valueB = valueB;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        return this.valueA.eval(context) - this.valueB.eval(context);
    }
    
    public IntFunction a() {
        return this.valueA;
    }
    
    public IntFunction b() {
        return this.valueB;
    }
    
    @Override
    public boolean isStatic() {
        return this.valueA.isStatic() && this.valueB.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.valueA.gameFlags(game) | this.valueB.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.valueA.preprocess(game);
        this.valueB.preprocess(game);
        if (this.isStatic()) {
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
}
