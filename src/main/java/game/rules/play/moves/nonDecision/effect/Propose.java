// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.others.ActionPropose;

public final class Propose extends Effect
{
    private static final long serialVersionUID = 1L;
    private final String[] propositions;
    
    public Propose(@Or final String proposition, @Or final String[] propositions, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (proposition != null) {
            ++numNonNull;
        }
        if (propositions != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one Or parameter can be non-null.");
        }
        if (propositions != null) {
            this.propositions = propositions;
        }
        else {
            (this.propositions = new String[1])[0] = proposition;
        }
    }
    
    @Override
    public Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
        for (final String proposition : this.propositions) {
            final Action action = new ActionPropose(proposition, true);
            if (this.isDecision()) {
                action.setDecision(true);
            }
            final Move move = new Move(action);
            move.setFromNonDecision(-1);
            move.setToNonDecision(-1);
            move.setMover(context.state().mover());
            moves.moves().add(move);
        }
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
        final long gameFlags = super.gameFlags(game) | 0x80000000L;
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        final boolean isStatic = false;
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
    }
}
