// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.state;

import annotations.Opt;
import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import util.Context;
import util.Move;
import util.action.state.ActionStoreStateInContext;

public final class RememberState extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public RememberState(@Opt final Then then) {
        super(then);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final ActionStoreStateInContext action = new ActionStoreStateInContext();
        final Move move = new Move(action);
        moves.moves().add(move);
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = 0x40000000L | super.gameFlags(game);
        return flags;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "StoreState";
    }
}
