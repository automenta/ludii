// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.context;

import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

public final class Between extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private static final Between INSTANCE;
    
    private Between() {
    }
    
    public static Between instance() {
        return Between.INSTANCE;
    }
    
    public static Between construct() {
        return Between.INSTANCE;
    }
    
    @Override
    public int eval(final Context context) {
        return context.between();
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
    public String toString() {
        return "Between()";
    }
    
    static {
        INSTANCE = new Between();
    }
}
