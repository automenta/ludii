// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.container.Container;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.types.play.RoleType;
import util.Context;

public final class HandSite extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction playerId;
    private final IntFunction siteFn;
    private int precomputedValue;
    
    public HandSite(@Or final IntFunction indexPlayer, @Or final RoleType role, @Opt final IntFunction site) {
        this.precomputedValue = -1;
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
        this.siteFn = ((site == null) ? new IntConstant(0) : site);
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        final int player = this.playerId.eval(context);
        final int index = this.siteFn.eval(context);
        for (final Container c : context.containers()) {
            if (c.isHand() && c.owner() == player) {
                return context.sitesFrom()[c.index()] + index;
            }
        }
        return -1;
    }
    
    @Override
    public boolean isStatic() {
        return this.siteFn.isStatic() && this.playerId.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0x4L | this.siteFn.gameFlags(game) | this.playerId.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.siteFn.preprocess(game);
        this.playerId.preprocess(game);
        if (this.isStatic()) {
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
}
