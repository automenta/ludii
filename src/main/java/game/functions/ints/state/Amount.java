// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.state;

import annotations.Or;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;

public final class Amount extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    final IntFunction playerFn;
    
    public Amount(@Or final RoleType role, @Or final Player player) {
        int numNonNull = 0;
        if (role != null) {
            ++numNonNull;
        }
        if (player != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        this.playerFn = ((player == null) ? new Id(null, role) : player.index());
    }
    
    @Override
    public int eval(final Context context) {
        final int player = this.playerFn.eval(context);
        if (player < context.game().players().size() && player > 0) {
            return context.state().amount(player);
        }
        return -1;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.playerFn.gameFlags(game) | 0x80000L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.playerFn.preprocess(game);
    }
    
    @Override
    public String toString() {
        return "Amount()";
    }
}
