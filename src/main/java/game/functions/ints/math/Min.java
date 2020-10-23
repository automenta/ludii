// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.math;

import game.Game;
import game.functions.intArray.IntArrayConstant;
import game.functions.intArray.IntArrayFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import util.Context;

public final class Min extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntArrayFunction array;
    
    public Min(final IntFunction valueA, final IntFunction valueB) {
        this.array = new IntArrayConstant(new IntFunction[] { valueA, valueB });
    }
    
    public Min(final IntArrayFunction array) {
        this.array = array;
    }
    
    @Override
    public int eval(final Context context) {
        final int[] values = this.array.eval(context);
        if (values.length == 0) {
            return -1;
        }
        int min = values[0];
        for (int i = 1; i < values.length; ++i) {
            min = Math.min(min, values[i]);
        }
        return min;
    }
    
    @Override
    public boolean isStatic() {
        return this.array.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.array.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.array.preprocess(game);
    }
}
