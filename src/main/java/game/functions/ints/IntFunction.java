// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints;

import game.types.state.GameType;
import util.Context;

public interface IntFunction extends GameType
{
    int eval(final Context context);
    
    boolean isHint();
}
