// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.player;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.others.ActionSetValueOfPlayer;

@Hide
public final class SetValue extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction playerId;
    private final IntFunction valueFn;
    
    public SetValue(@Or final Player player, @Or final RoleType role, final IntFunction value, @Opt final Then then) {
        super(then);
        if (player != null) {
            this.playerId = player.index();
        }
        else {
            this.playerId = new Id(null, role);
        }
        this.valueFn = value;
    }
    
    @Override
    public Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
        final int pid = this.playerId.eval(context);
        final int value = this.valueFn.eval(context);
        final Action action = new ActionSetValueOfPlayer(pid, value);
        final Move move = new Move(action);
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
    public long gameFlags(final Game game) {
        final long gameFlags = this.playerId.gameFlags(game) | this.valueFn.gameFlags(game) | super.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.playerId.preprocess(game);
        this.valueFn.preprocess(game);
    }
}
