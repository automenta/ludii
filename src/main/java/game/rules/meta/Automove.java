// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.meta;

import annotations.Opt;
import game.Game;
import util.Context;

public class Automove extends MetaRule
{
    private static final long serialVersionUID = 1L;
    private final boolean value;
    
    public Automove(@Opt final Boolean value) {
        this.value = (value == null || value);
    }
    
    @Override
    public void eval(final Context context) {
        context.game().setAutomove(this.value);
    }
    
    @Override
    public long gameFlags(final Game game) {
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
