// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.value;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import util.Context;
import util.Move;
import util.action.state.ActionSetPot;

@Hide
public final class SetPot extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction value;
    
    public SetPot(@Opt final IntFunction value, @Opt final Then then) {
        super(then);
        this.value = ((value == null) ? new IntConstant(-1) : value);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final ActionSetPot actionSetPot = new ActionSetPot(this.value.eval(context));
        final Move move = new Move(actionSetPot);
        result.moves().add(move);
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
    }
    
    @Override
    public String toEnglish() {
        return "SetPot";
    }
}
