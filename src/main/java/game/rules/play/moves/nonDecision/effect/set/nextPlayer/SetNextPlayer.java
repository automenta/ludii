// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.nextPlayer;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.intArray.IntArrayConstant;
import game.functions.intArray.IntArrayFunction;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.util.moves.Player;
import util.Context;
import util.Move;
import util.action.state.ActionSetNextPlayer;

@Hide
public final class SetNextPlayer extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntArrayFunction nextPlayerFn;
    
    public SetNextPlayer(@Or final Player who, @Or final IntArrayFunction nextPlayers, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (who != null) {
            ++numNonNull;
        }
        if (nextPlayers != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one Or parameter can be non-null.");
        }
        if (nextPlayers != null) {
            this.nextPlayerFn = nextPlayers;
        }
        else {
            this.nextPlayerFn = new IntArrayConstant(new IntFunction[] { who.index() });
        }
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int[] eval;
        final int[] nextPlayerIds = eval = this.nextPlayerFn.eval(context);
        for (final int nextPlayerId : eval) {
            if (nextPlayerId < 1 || nextPlayerId > context.game().players().count()) {
                System.err.println("The Player " + nextPlayerId + " can not be set");
            }
            else {
                final ActionSetNextPlayer actionSetNextPlayer = new ActionSetNextPlayer(nextPlayerId);
                if (this.isDecision()) {
                    actionSetNextPlayer.setDecision(true);
                }
                final Move move = new Move(actionSetNextPlayer);
                move.setFromNonDecision(-1);
                move.setToNonDecision(-1);
                move.setMover(context.state().mover());
                moves.moves().add(move);
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
        return super.gameFlags(game) | this.nextPlayerFn.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return this.nextPlayerFn.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.nextPlayerFn.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "Replay";
    }
}
