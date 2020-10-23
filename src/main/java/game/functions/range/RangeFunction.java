// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.range;

import game.functions.ints.IntFunction;
import game.types.state.GameType;
import util.Context;

public interface RangeFunction extends GameType
{
    Range eval(final Context context);
    
    IntFunction minFn();
    
    IntFunction maxFn();
}
