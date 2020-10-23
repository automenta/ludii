// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import util.Context;
import util.Move;
import util.action.others.ActionPass;

public final class Pass extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public Pass(@Opt final Then then) {
        super(then);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final ActionPass actionPass = new ActionPass();
        if (this.isDecision()) {
            actionPass.setDecision(true);
        }
        final Move move = new Move(actionPass);
        move.setFromNonDecision(-1);
        move.setToNonDecision(-1);
        move.setMover(context.state().mover());
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
        long gameFlags = super.gameFlags(game);
        if (game.players().count() == 1) {
            gameFlags |= 0x1000L;
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return super.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
    }
}
