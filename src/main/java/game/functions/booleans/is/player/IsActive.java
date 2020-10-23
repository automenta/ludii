// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.player;

import annotations.Hide;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import util.Context;

@Hide
public final class IsActive extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction playerId;
    
    public IsActive(@Or final IntFunction indexPlayer, @Or final RoleType role) {
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
    }
    
    @Override
    public boolean eval(final Context context) {
        final int roleId = this.playerId.eval(context);
        return roleId != 0 && roleId <= context.game().players().count() && context.active(roleId);
    }
    
    @Override
    public boolean isStatic() {
        return this.playerId.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.playerId.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.playerId.preprocess(game);
    }
    
    public IntFunction role() {
        return this.playerId;
    }
}
