// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.range.RangeFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.play.ModeType;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;
import util.Move;
import util.action.state.ActionBet;

public final class Bet extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction playerFn;
    private final RangeFunction range;
    
    public Bet(@Or final Player who, @Or final RoleType role, final RangeFunction range, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (who != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Piece(): One who or role parameter must be non-null.");
        }
        this.range = range;
        this.playerFn = ((role != null) ? new Id(null, role) : who.index());
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int player = this.playerFn.eval(context);
        final int min = this.range.minFn().eval(context);
        for (int max = this.range.maxFn().eval(context), i = min; i <= max; ++i) {
            final ActionBet actionBet = new ActionBet(player, i);
            if (this.isDecision()) {
                actionBet.setDecision(true);
            }
            final Move move = new Move(actionBet);
            move.setDecision(true);
            move.setFromNonDecision(-1);
            move.setToNonDecision(-1);
            if (context.game().mode().mode() == ModeType.Simultaneous) {
                move.setMover(player);
            }
            else {
                move.setMover(context.state().mover());
            }
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
        return super.gameFlags(game) | this.range.gameFlags(game) | this.playerFn.gameFlags(game) | 0x80000L;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.range.preprocess(game);
        this.playerFn.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "Bet";
    }
}
