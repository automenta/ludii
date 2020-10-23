// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.was;

import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import util.Context;

public class Was extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    public static BooleanFunction construct(final WasType wasType) {
        switch (wasType) {
            case Pass -> {
                return new WasPass();
            }
            default -> throw new IllegalArgumentException("Was(): A wasType is not implemented.");
        }
    }
    
    private Was() {
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
        throw new UnsupportedOperationException("Was.eval(): Should never be called directly.");
    }
}
