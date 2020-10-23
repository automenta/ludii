// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.card.simple;

import annotations.Hide;
import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

@Hide
public final class CardTrumpSuit extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public int eval(final Context context) {
        return context.state().trumpSuit();
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 8264L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
