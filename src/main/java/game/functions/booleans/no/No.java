// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.no;

import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import game.types.play.RoleType;
import util.Context;

public class No extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    public static BooleanFunction construct(final NoType noType, final RoleType playerFn) {
        switch (noType) {
            case Moves -> {
                return new NoMoves(playerFn);
            }
            default -> throw new IllegalArgumentException("No(): A NoType is not implemented.");
        }
    }
    
    private No() {
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
        throw new UnsupportedOperationException("No.eval(): Should never be called directly.");
    }
}
