// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.math;

import annotations.Alias;
import annotations.Or;
import game.Game;
import game.functions.intArray.IntArrayConstant;
import game.functions.intArray.IntArrayFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import util.Context;

@Alias(alias = "*")
public final class Mul extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntArrayFunction array;
    private int precomputedValue;
    
    public Mul(final IntFunction a, final IntFunction b) {
        this.precomputedValue = -1;
        this.array = new IntArrayConstant(new IntFunction[] { a, b });
    }
    
    public Mul(@Or final IntFunction[] list, @Or final IntArrayFunction array) {
        this.precomputedValue = -1;
        int numNonNull = 0;
        if (list != null) {
            ++numNonNull;
        }
        if (array != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Mul(): One 'list' or 'array' parameters must be non-null.");
        }
        this.array = ((array != null) ? array : new IntArrayConstant(list));
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        final int[] values = this.array.eval(context);
        if (values.length == 0) {
            return 0;
        }
        int product = 1;
        for (final int val : values) {
            product *= val;
        }
        return product;
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
        if (this.isStatic()) {
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
}
