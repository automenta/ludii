// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.pending;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import util.Context;
import util.Move;
import util.action.state.ActionSetPending;

@Hide
public final class SetPending extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction value;
    private final RegionFunction region;
    
    public SetPending(@Opt @Or final IntFunction value, @Opt @Or final RegionFunction region, @Opt final Then then) {
        super(then);
        this.value = value;
        this.region = region;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        if (this.region == null) {
            final ActionSetPending actionPending = (this.value == null) ? new ActionSetPending(-1) : new ActionSetPending(this.value.eval(context));
            final Move move = new Move(actionPending);
            result.moves().add(move);
        }
        else {
            final int[] sites = this.region.eval(context).sites();
            if (sites.length != 0) {
                final ActionSetPending actionPending = new ActionSetPending(sites[0]);
                final Move move2 = new Move(actionPending);
                for (int i = 1; i < sites.length; ++i) {
                    final ActionSetPending actionToadd = new ActionSetPending(sites[i]);
                    move2.actions().add(actionToadd);
                }
                result.moves().add(move2);
            }
        }
        return result;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        if (this.value != null) {
            gameFlags = this.value.gameFlags(game);
        }
        if (this.region != null) {
            gameFlags = this.region.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        if (this.value != null) {
            this.value.preprocess(game);
        }
        if (this.region != null) {
            this.region.preprocess(game);
        }
    }
    
    @Override
    public String toEnglish() {
        return "Pending";
    }
}
