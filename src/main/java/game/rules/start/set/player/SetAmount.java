// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.set.player;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.start.StartRule;
import game.types.play.RoleType;
import util.Context;
import util.Move;
import util.action.state.ActionSetAmount;

@Hide
public final class SetAmount extends StartRule
{
    private static final long serialVersionUID = 1L;
    protected final IntFunction playersFn;
    protected final IntFunction amountFn;
    
    public SetAmount(@Opt final RoleType role, final IntFunction amount) {
        if (role != null) {
            this.playersFn = new Id(null, role);
        }
        else {
            this.playersFn = null;
        }
        this.amountFn = amount;
    }
    
    @Override
    public void eval(final Context context) {
        final int amount = this.amountFn.eval(context);
        int[] players;
        if (this.playersFn != null) {
            players = new int[] { this.playersFn.eval(context) };
        }
        else {
            players = new int[context.game().players().count()];
            for (int i = 0; i < players.length; ++i) {
                players[i] = i + 1;
            }
        }
        for (int i = 0; i < players.length; ++i) {
            final int playerId = players[i];
            final ActionSetAmount actionAmount = new ActionSetAmount(playerId, amount);
            actionAmount.apply(context, true);
            final Move move = new Move(actionAmount);
            context.trial().moves().add(move);
            context.trial().addInitPlacement();
        }
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0x80000L | this.amountFn.gameFlags(game);
        if (this.playersFn != null) {
            gameFlags |= this.playersFn.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.playersFn != null) {
            this.playersFn.preprocess(game);
        }
        this.amountFn.preprocess(game);
    }
    
    @Override
    public String toString() {
        final String str = "initAmount ";
        return "initAmount ";
    }
    
    @Override
    public boolean isSet() {
        return false;
    }
}
