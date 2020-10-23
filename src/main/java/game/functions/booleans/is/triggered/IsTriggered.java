// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.triggered;

import annotations.Hide;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import util.Context;

@Hide
public final class IsTriggered extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction playerId;
    private final String event;
    
    public IsTriggered(final String event, @Or final IntFunction indexPlayer, @Or final RoleType role) {
        int numNonNull = 0;
        if (indexPlayer != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (indexPlayer != null) {
            this.playerId = indexPlayer;
        }
        else {
            this.playerId = new Id(null, role);
        }
        this.event = event;
    }
    
    @Override
    public final boolean eval(final Context context) {
        final int pid = this.playerId.eval(context);
        return context.active(pid) && context.state().isTriggered(this.event, pid);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.playerId.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.playerId.preprocess(game);
    }
}
