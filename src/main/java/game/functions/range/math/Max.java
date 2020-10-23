// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.range.math;

import game.Game;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.range.BaseRangeFunction;
import game.functions.range.Range;
import util.Context;

public final class Max extends BaseRangeFunction
{
    private static final long serialVersionUID = 1L;
    
    public Max(final IntFunction max) {
        super(new IntConstant(-1), max);
    }
    
    @Override
    public Range eval(final Context context) {
        if (this.precomputedRange != null) {
            return this.precomputedRange;
        }
        return new Range(this.minFn.eval(context), this.maxFn.eval(context));
    }
    
    @Override
    public boolean isStatic() {
        return this.minFn.isStatic() && this.maxFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.minFn.gameFlags(game) | this.maxFn.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.isStatic()) {
            this.precomputedRange = this.eval(new Context(game, null));
        }
    }
}
