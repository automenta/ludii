// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.all;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import util.Context;

@Hide
public final class AllDiceUsed extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean eval(final Context context) {
        final int[][] diceValues = context.state().currentDice();
        for (int[] diceValue : diceValues) {
            for (int indexDie = 0; indexDie < diceValue.length; ++indexDie) {
                if (diceValue[indexDie] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "AllDiceUsed()";
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
    }
}
