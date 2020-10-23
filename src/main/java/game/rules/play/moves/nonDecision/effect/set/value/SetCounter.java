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
import util.action.BaseAction;
import util.action.state.ActionSetCounter;

@Hide
public final class SetCounter extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction newValue;
    
    public SetCounter(@Opt final IntFunction newValue, @Opt final Then then) {
        super(then);
        this.newValue = ((newValue == null) ? new IntConstant(-1) : newValue);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final BaseAction actionSetCounter = new ActionSetCounter(this.newValue.eval(context));
        final Move move = new Move(actionSetCounter);
        result.moves().add(move);
        return result;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.newValue.gameFlags(game) | super.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return this.newValue.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.newValue.preprocess(game);
    }
    
    @Override
    public String toString() {
        return "SetCounter(" + this.newValue + ") Generator";
    }
    
    @Override
    public String toEnglish() {
        return "SetCounter";
    }
}
