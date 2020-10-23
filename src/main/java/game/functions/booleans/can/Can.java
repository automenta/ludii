// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.can;

import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import game.rules.play.moves.Moves;
import util.Context;

public class Can extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    public static BooleanFunction construct(final CanType canType, final Moves moves) {
        switch (canType) {
            case Move -> {
                return new CanMove(moves);
            }
            default -> {
                throw new IllegalArgumentException("Can(): A CanType is not implemented.");
            }
        }
    }
    
    private Can() {
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
    
    @Override
    public boolean eval(final Context context) {
        throw new UnsupportedOperationException("Can.eval(): Should never be called directly.");
    }
}
