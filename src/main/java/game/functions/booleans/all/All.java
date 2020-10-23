// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.all;

import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import util.Context;

public class All extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    public static BooleanFunction construct(final AllType allType) {
        switch (allType) {
            case DiceUsed -> {
                return new AllDiceUsed();
            }
            case Passed -> {
                return new AllPassed();
            }
            case DiceEqual -> {
                return new AllDiceEqual();
            }
            default -> {
                throw new IllegalArgumentException("All(): A AllType is not implemented.");
            }
        }
    }
    
    private All() {
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public boolean eval(final Context context) {
        throw new UnsupportedOperationException("All.eval(): Should never be called directly.");
    }
}
