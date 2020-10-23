// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.dim;

import annotations.Alias;
import game.Game;
import util.BaseLudeme;

@Alias(alias = "dim")
public abstract class BaseDimFunction extends BaseLudeme implements DimFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
