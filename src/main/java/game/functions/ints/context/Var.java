// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.context;

import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

public final class Var extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final String key;
    
    public Var(@Opt final String key) {
        this.key = key;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.key == null) {
            return context.state().temp();
        }
        return context.getValue(this.key);
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
        return "GetVariable [key=" + this.key + "]";
    }
}
