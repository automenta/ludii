// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.state.swap;

import annotations.And;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.functions.ints.IntFunction;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.effect.state.swap.players.SwapPlayers;
import game.rules.play.moves.nonDecision.effect.state.swap.sites.SwapPieces;
import game.types.play.RoleType;
import util.Context;

public final class Swap extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public static Moves construct(final SwapSitesType swapType, @Opt final IntFunction locA, @Opt final IntFunction locB, @Opt final Then then) {
        switch (swapType) {
            case Pieces: {
                return new SwapPieces(locA, locB, then);
            }
            default: {
                throw new IllegalArgumentException("Swap(): A SwapSitesType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final SwapPlayersType takeType, @And @Or final IntFunction player1, @And @Or final RoleType role1, @And @Or2 final IntFunction player2, @And @Or2 final RoleType role2, @Opt final Then then) {
        int numNonNull = 0;
        if (player1 != null) {
            ++numNonNull;
        }
        if (role1 != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Swap(): Exactly one player1 or role1 parameter must be non-null.");
        }
        int numNonNull2 = 0;
        if (player2 != null) {
            ++numNonNull2;
        }
        if (role2 != null) {
            ++numNonNull2;
        }
        if (numNonNull2 != 1) {
            throw new IllegalArgumentException("Swap(): Exactly one player2 or role2 parameter must be non-null.");
        }
        switch (takeType) {
            case Players: {
                return new SwapPlayers(player1, role1, player2, role2, then);
            }
            default: {
                throw new IllegalArgumentException("Swap(): A SwapSitesType is not implemented.");
            }
        }
    }
    
    private Swap() {
        super(null);
    }
    
    @Override
    public Moves eval(final Context context) {
        throw new UnsupportedOperationException("Swap.eval(): Should never be called directly.");
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return super.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        throw new UnsupportedOperationException("Swap.canMoveTo(): Should never be called directly.");
    }
}
