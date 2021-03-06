// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.meta;

import annotations.Opt;
import game.Game;
import util.Context;

public class Swap extends MetaRule
{
    private static final long serialVersionUID = 1L;
    private final boolean value;
    
    public Swap(@Opt final Boolean value) {
        this.value = (value == null || value);
    }
    
    @Override
    public void eval(final Context context) {
    }
    
    @Override
    public long gameFlags(final Game game) {
        if (this.value) {
            return 1099511627776L;
        }
        return 0L;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
