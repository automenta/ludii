// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.play.RoleType;
import util.Context;
import util.Move;
import util.action.state.ActionTrigger;

public final class Trigger extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction playerFunction;
    private final String event;
    
    public Trigger(final String event, @Or final IntFunction indexPlayer, @Or final RoleType role, @Opt final Then then) {
        super(then);
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
            this.playerFunction = indexPlayer;
        }
        else {
            this.playerFunction = new Id(null, role);
        }
        this.event = event;
    }
    
    @Override
    public Moves eval(final Context context) {
        final int victim = this.playerFunction.eval(context);
        final Moves moves = new BaseMoves(super.then());
        moves.moves().add(new Move(new ActionTrigger(this.event, victim)));
        return moves;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.playerFunction.gameFlags(game) | super.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return this.playerFunction.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.playerFunction.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "Trigger";
    }
}
