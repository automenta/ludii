// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.integer;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import util.Context;

@Hide
public final class IsAnyDie extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction valueFn;
    
    public IsAnyDie(final IntFunction value) {
        this.valueFn = value;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.valueFn == null) {
            return false;
        }
        final int value = this.valueFn.eval(context);
        final int[] currentDice;
        final int[] dieValues = currentDice = context.state().currentDice(0);
        for (final int dieValue : currentDice) {
            if (value == dieValue) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "IsDie";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 64L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.valueFn.preprocess(game);
    }
}
