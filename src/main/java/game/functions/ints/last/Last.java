// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.last;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import util.Context;

public final class Last extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    public static IntFunction construct(final LastType lastType, @Opt @Name final BooleanFunction afterConsequence) {
        switch (lastType) {
            case From: {
                return new LastFrom(afterConsequence);
            }
            case To: {
                return new LastTo(afterConsequence);
            }
            default: {
                throw new IllegalArgumentException("Last(): A LastType is not implemented.");
            }
        }
    }
    
    private Last() {
    }
    
    @Override
    public int eval(final Context context) {
        throw new UnsupportedOperationException("Last.eval(): Should never be called directly.");
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
}
