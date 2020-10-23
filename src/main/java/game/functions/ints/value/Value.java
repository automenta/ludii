// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.value;

import annotations.Name;
import annotations.Or;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.value.iterated.ValueIterated;
import game.functions.ints.value.piece.ValuePiece;
import game.functions.ints.value.player.ValuePlayer;
import game.functions.ints.value.simple.ValuePending;
import game.types.play.RoleType;
import util.Context;

public final class Value extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    public static IntFunction construct(final ValueSimpleType valueType) {
        switch (valueType) {
            case Pending -> {
                return new ValuePending();
            }
            default -> throw new IllegalArgumentException("Value(): A ValueSimpleType is not implemented.");
        }
    }
    
    public static IntFunction construct(final ValuePlayerType valueType, @Or final IntFunction indexPlayer, @Or final RoleType role) {
        int numNonNull = 0;
        if (indexPlayer != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Value(): With ValuePlayerType exactly one indexPlayer or role parameter must be non-null.");
        }
        switch (valueType) {
            case Player -> {
                return new ValuePlayer(indexPlayer, role);
            }
            default -> throw new IllegalArgumentException("Value(): A ValuePlayerType is not implemented.");
        }
    }
    
    public static IntFunction construct(final ValueComponentType valueType, @Name final IntFunction of) {
        switch (valueType) {
            case Piece -> {
                return new ValuePiece(of);
            }
            default -> throw new IllegalArgumentException("Value(): A ValueComponentType is not implemented.");
        }
    }
    
    public static IntFunction construct() {
        return new ValueIterated();
    }
    
    private Value() {
    }
    
    @Override
    public int eval(final Context context) {
        throw new UnsupportedOperationException("Value.eval(): Should never be called directly.");
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
