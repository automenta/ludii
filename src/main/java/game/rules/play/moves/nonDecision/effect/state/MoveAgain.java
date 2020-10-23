// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.state;

import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import util.Context;
import util.Move;
import util.action.state.ActionSetNextPlayer;
import util.state.State;

public final class MoveAgain extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public MoveAgain() {
        super(null);
    }
    
    @Override
    public Moves eval(final Context context) {
        final State state = context.state();
        final Moves move = new BaseMoves(super.then());
        move.moves().add(new Move(new ActionSetNextPlayer(state.mover())));
        return move;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0x20000000L | super.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "MoveAgain";
    }
}
