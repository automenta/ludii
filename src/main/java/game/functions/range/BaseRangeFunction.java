// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.range;

import annotations.Hide;
import game.functions.ints.IntFunction;
import util.BaseLudeme;
import util.Context;

@Hide
public abstract class BaseRangeFunction extends BaseLudeme implements RangeFunction
{
    private static final long serialVersionUID = 1L;
    protected final IntFunction minFn;
    protected final IntFunction maxFn;
    protected Range precomputedRange;
    
    public BaseRangeFunction(final IntFunction min, final IntFunction max) {
        this.precomputedRange = null;
        this.minFn = min;
        this.maxFn = max;
    }
    
    @Override
    public Range eval(final Context context) {
        System.out.println("BaseRangeFunction.eval(): Should not be called directly; call subclass.");
        return null;
    }
    
    @Override
    public IntFunction minFn() {
        return this.minFn;
    }
    
    @Override
    public IntFunction maxFn() {
        return this.maxFn;
    }
}
