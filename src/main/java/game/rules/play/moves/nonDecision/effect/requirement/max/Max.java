// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.requirement.max;

import annotations.Opt;
import game.Game;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.effect.requirement.max.distance.MaxDistance;
import game.rules.play.moves.nonDecision.effect.requirement.max.moves.MaxCaptures;
import game.rules.play.moves.nonDecision.effect.requirement.max.moves.MaxMoves;
import game.types.play.RoleType;
import util.Context;

public final class Max extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public static Moves construct(final MaxMovesType maxType, final Moves moves, @Opt final Then then) {
        switch (maxType) {
            case Captures -> {
                return new MaxCaptures(moves, then);
            }
            case Moves -> {
                return new MaxMoves(moves, then);
            }
            default -> throw new IllegalArgumentException("Max(): A MaxMovesType is not implemented.");
        }
    }
    
    public static Moves construct(final MaxDistanceType maxType, @Opt final String trackName, @Opt final RoleType owner, final Moves moves, @Opt final Then then) {
        switch (maxType) {
            case Distance -> {
                return new MaxDistance(trackName, owner, moves, then);
            }
            default -> throw new IllegalArgumentException("Max(): A MaxDistanceType is not implemented.");
        }
    }
    
    private Max() {
        super(null);
    }
    
    @Override
    public Moves eval(final Context context) {
        throw new UnsupportedOperationException("Max.eval(): Should never be called directly.");
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
        throw new UnsupportedOperationException("Max.canMoveTo(): Should never be called directly.");
    }
}
