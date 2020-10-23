// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.simple;

import annotations.Hide;
import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

@Hide
public final class CountActive extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public int eval(final Context context) {
        int count = 0;
        for (int i = 1; i < context.game().players().size(); ++i) {
            if (context.active(i)) {
                ++count;
            }
        }
        return count;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Active()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
