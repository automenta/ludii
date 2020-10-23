// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.intArray.math;

import annotations.Or;
import game.Game;
import game.functions.intArray.BaseIntArrayFunction;
import game.functions.ints.IntFunction;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

public final class Difference extends BaseIntArrayFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction[] source;
    private final IntFunction[] subtraction;
    private final IntFunction intToRemove;
    private int[] precomputedRegion;
    
    public Difference(final IntFunction[] source, @Or final IntFunction[] subtraction, @Or final IntFunction intToRemove) {
        this.precomputedRegion = null;
        this.source = source;
        int numNonNull = 0;
        if (subtraction != null) {
            ++numNonNull;
        }
        if (intToRemove != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Only one Or parameter must be non-null.");
        }
        this.intToRemove = intToRemove;
        this.subtraction = subtraction;
    }
    
    @Override
    public int[] eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final int[] arraySource = new int[this.source.length];
        for (int i = 0; i < this.source.length; ++i) {
            final IntFunction sourceFunction = this.source[i];
            arraySource[i] = sourceFunction.eval(context);
        }
        final TIntArrayList sourcelist = new TIntArrayList(arraySource);
        if (this.subtraction != null) {
            final int[] subSource = new int[this.subtraction.length];
            for (int j = 0; j < this.subtraction.length; ++j) {
                final IntFunction subFunction = this.subtraction[j];
                subSource[j] = subFunction.eval(context);
            }
            final TIntArrayList subList = new TIntArrayList(subSource);
            sourcelist.removeAll(subList);
        }
        else {
            final int integerToRemove = this.intToRemove.eval(context);
            sourcelist.remove(integerToRemove);
        }
        return sourcelist.toArray();
    }
    
    @Override
    public boolean isStatic() {
        for (final IntFunction sourceFunction : this.source) {
            if (!sourceFunction.isStatic()) {
                return false;
            }
        }
        if (this.subtraction != null) {
            for (final IntFunction subFunction : this.subtraction) {
                if (!subFunction.isStatic()) {
                    return false;
                }
            }
        }
        return this.intToRemove == null || this.intToRemove.isStatic();
    }
    
    @Override
    public String toString() {
        return "Difference(" + this.source + "," + this.subtraction + ")";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flag = 0L;
        for (final IntFunction sourceFunction : this.source) {
            flag |= sourceFunction.gameFlags(game);
        }
        if (this.subtraction != null) {
            for (final IntFunction subFunction : this.subtraction) {
                flag |= subFunction.gameFlags(game);
            }
        }
        if (this.intToRemove != null) {
            flag |= this.intToRemove.gameFlags(game);
        }
        return flag;
    }
    
    @Override
    public void preprocess(final Game game) {
        for (final IntFunction sourceFunction : this.source) {
            sourceFunction.preprocess(game);
        }
        if (this.subtraction != null) {
            for (final IntFunction subFunction : this.subtraction) {
                subFunction.preprocess(game);
            }
        }
        if (this.intToRemove != null) {
            this.intToRemove.preprocess(game);
        }
        if (this.isStatic()) {
            final Context context = new Context(game, null);
            final int[] arraySource = new int[this.source.length];
            for (int j = 0; j < this.source.length; ++j) {
                final IntFunction sourceFunction2 = this.source[j];
                arraySource[j] = sourceFunction2.eval(context);
            }
            final TIntArrayList sourcelist = new TIntArrayList(arraySource);
            if (this.subtraction != null) {
                final int[] subSource = new int[this.subtraction.length];
                for (int k = 0; k < this.subtraction.length; ++k) {
                    final IntFunction subFunction2 = this.subtraction[k];
                    subSource[k] = subFunction2.eval(context);
                }
                final TIntArrayList subList = new TIntArrayList(subSource);
                sourcelist.removeAll(subList);
            }
            else {
                final int integerToRemove = this.intToRemove.eval(context);
                sourcelist.remove(integerToRemove);
            }
            this.precomputedRegion = sourcelist.toArray();
        }
    }
}
