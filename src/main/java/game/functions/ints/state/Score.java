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

public final class Score extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction playerFn;
    
    public Score(@Or final Player player, @Or final RoleType role) {
        int numNonNull = 0;
        if (player != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (player != null) {
            this.playerFn = player.index();
        }
        else {
            this.playerFn = new Id(null, role);
        }
    }
    
    @Override
    public int eval(final Context context) {
        return context.score(this.playerFn.eval(context));
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long gameFlags = 256L;
        return 0x100L | this.playerFn.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.playerFn.preprocess(game);
    }
}
