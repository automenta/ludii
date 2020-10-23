// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.intArray;

import game.Game;
import game.functions.ints.IntFunction;
import util.Context;

public final class IntArrayConstant extends BaseIntArrayFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction[] ints;
    
    public IntArrayConstant(final IntFunction[] ints) {
        this.ints = ints;
    }
    
    @Override
    public int[] eval(final Context context) {
        final int[] toReturn = new int[this.ints.length];
        for (int i = 0; i < this.ints.length; ++i) {
            final IntFunction intFunction = this.ints[i];
            toReturn[i] = intFunction.eval(context);
        }
        return toReturn;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < this.ints.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("" + this.ints[i]);
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public boolean isStatic() {
        for (final IntFunction function : this.ints) {
            if (!function.isStatic()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        for (final IntFunction function : this.ints) {
            flags |= function.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        for (final IntFunction function : this.ints) {
            function.preprocess(game);
        }
    }
}
