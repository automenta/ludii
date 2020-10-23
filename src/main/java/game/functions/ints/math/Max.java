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

public final class Max extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntArrayFunction array;
    
    public Max(final IntFunction valueA, final IntFunction valueB) {
        this.array = new IntArrayConstant(new IntFunction[] { valueA, valueB });
    }
    
    public Max(final IntArrayFunction array) {
        this.array = array;
    }
    
    @Override
    public int eval(final Context context) {
        final int[] values = this.array.eval(context);
        if (values.length == 0) {
            return -1;
        }
        int max = values[0];
        for (int i = 1; i < values.length; ++i) {
            max = Math.max(max, values[i]);
        }
        return max;
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
