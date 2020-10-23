// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints;

import util.BaseLudeme;

public abstract class BaseIntFunction extends BaseLudeme implements IntFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean isHint() {
        return false;
    }
}
