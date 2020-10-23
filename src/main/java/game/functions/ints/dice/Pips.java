// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.dice;

import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

public final class Pips extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private static final Pips INSTANCE;
    
    public static Pips construct() {
        return Pips.INSTANCE;
    }
    
    private Pips() {
    }
    
    public static Pips instance() {
        return Pips.INSTANCE;
    }
    
    @Override
    public int eval(final Context context) {
        return context.pipCount();
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
        return "Pips()";
    }
    
    static {
        INSTANCE = new Pips();
    }
}
