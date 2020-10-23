// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.suit;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.intArray.IntArrayConstant;
import game.functions.intArray.IntArrayFunction;
import game.functions.intArray.math.Difference;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.cards.ActionSetTrumpSuit;

@Hide
public final class SetTrumpSuit extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntArrayFunction suitsFn;
    
    public SetTrumpSuit(@Or final IntFunction suit, @Or final Difference suits, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (suit != null) {
            ++numNonNull;
        }
        if (suits != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Only one Or parameter must be non-null.");
        }
        if (suits != null) {
            this.suitsFn = suits;
        }
        else {
            this.suitsFn = new IntArrayConstant(new IntFunction[] { suit });
        }
    }
    
    @Override
    public Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
        final int[] eval;
        final int[] suits = eval = this.suitsFn.eval(context);
        for (final int suit : eval) {
            final Action action = new ActionSetTrumpSuit(suit);
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
        long gameFlags = super.gameFlags(game) | 0x1000L;
        gameFlags |= this.suitsFn.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.suitsFn.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
    }
}
