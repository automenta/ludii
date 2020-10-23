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
import gnu.trove.list.array.TIntArrayList;
import util.Context;

@Hide
public final class IsFriend extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction playerId;
    
    public IsFriend(@Or final IntFunction indexPlayer, @Or final RoleType role) {
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
        this.playerId = ((role != null) ? new Id(null, role) : indexPlayer);
    }
    
    @Override
    public boolean eval(final Context context) {
        if (context.game().requiresTeams()) {
            final TIntArrayList teamMembers = new TIntArrayList();
            final int tid = context.state().getTeam(context.state().mover());
            for (int i = 1; i < context.game().players().size(); ++i) {
                if (context.state().getTeam(i) == tid) {
                    teamMembers.add(i);
                }
            }
            return teamMembers.contains(this.playerId.eval(context));
        }
        return this.playerId.eval(context) == context.state().mover() || context.state().mover() == context.game().players().size();
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
