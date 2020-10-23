// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.range;

import game.Game;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import util.Context;

public final class Range extends BaseRangeFunction
{
    private static final long serialVersionUID = 1L;
    
    public Range(final IntFunction min, final IntFunction max) {
        super(min, max);
    }
    
    public Range(final Integer min, final Integer max) {
        super(new IntConstant(min), new IntConstant(max));
    }
    
    @Override
    public Range eval(final Context context) {
        if (this.precomputedRange != null) {
            return this.precomputedRange;
        }
        return this;
    }
    
    public int min() {
        return this.minFn.eval(null);
    }
    
    public int max() {
        return this.maxFn.eval(null);
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
