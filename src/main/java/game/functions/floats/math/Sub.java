// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.floats.math;

import annotations.Alias;
import game.Game;
import game.functions.floats.BaseFloatFunction;
import game.functions.floats.FloatFunction;
import util.Context;

@Alias(alias = "-")
public final class Sub extends BaseFloatFunction
{
    private static final long serialVersionUID = 1L;
    private final FloatFunction valueA;
    private final FloatFunction valueB;
    
    public Sub(final FloatFunction valueA, final FloatFunction valueB) {
        this.valueA = valueA;
        this.valueB = valueB;
    }
    
    @Override
    public float eval(final Context context) {
        return this.valueA.eval(context) - this.valueB.eval(context);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.valueA.gameFlags(game) | this.valueB.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.valueA.preprocess(game);
        this.valueB.preprocess(game);
    }
}
