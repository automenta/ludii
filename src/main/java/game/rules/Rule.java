// 
// Decompiled by Procyon v0.5.36
// 

package game.rules;

import game.types.state.GameType;
import util.Context;
import util.Ludeme;

public interface Rule extends GameType, Ludeme
{
    void eval(final Context context);
}
