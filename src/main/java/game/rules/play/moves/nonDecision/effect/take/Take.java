// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.take;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.functions.ints.IntFunction;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.effect.take.control.TakeControl;
import game.rules.play.moves.nonDecision.effect.take.simple.TakeDomino;
import game.types.board.SiteType;
import game.types.play.RoleType;
import util.Context;

public final class Take extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public static Moves construct(final TakeSimpleType takeType, @Opt final Then then) {
        switch (takeType) {
            case Domino -> {
                return new TakeDomino(then);
            }
            default -> {
                throw new IllegalArgumentException("Take(): A TakeSimpleType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final TakeControlType takeType, @Or @Name final RoleType of, @Or @Name final IntFunction OF, @Or2 @Name final RoleType by, @Or2 @Name final IntFunction BY, @Opt final SiteType type, @Opt final Then then) {
        switch (takeType) {
            case Control -> {
                return new TakeControl(of, OF, by, BY, type, then);
            }
            default -> {
                throw new IllegalArgumentException("Take(): A TakeControlType is not implemented.");
            }
        }
    }
    
    private Take() {
        super(null);
    }
    
    @Override
    public Moves eval(final Context context) {
        throw new UnsupportedOperationException("Take.eval(): Should never be called directly.");
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
    public boolean canMoveTo(final Context context, final int target) {
        throw new UnsupportedOperationException("Take.canMoveTo(): Should never be called directly.");
    }
}
