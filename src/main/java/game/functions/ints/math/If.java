// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.math;

import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import util.Context;

public final class If extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final BooleanFunction cond;
    private final IntFunction valueA;
    private final IntFunction valueB;
    
    public If(final BooleanFunction cond, final IntFunction valueA, final IntFunction valueB) {
        this.cond = cond;
        this.valueA = valueA;
        this.valueB = valueB;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.cond.eval(context)) {
            return this.valueA.eval(context);
        }
        return this.valueB.eval(context);
    }
    
    @Override
    public boolean isStatic() {
        return this.valueA.isStatic() && this.valueB.isStatic() && this.cond.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.cond.gameFlags(game) | this.valueA.gameFlags(game) | this.valueB.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.cond.preprocess(game);
        this.valueA.preprocess(game);
        this.valueB.preprocess(game);
    }
}
