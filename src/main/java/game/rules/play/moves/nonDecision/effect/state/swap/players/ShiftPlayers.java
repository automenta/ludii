// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.state.swap.players;

import annotations.*;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.play.RoleType;
import util.Context;
import util.Move;
import util.action.others.ActionSwap;
import util.action.state.ActionSetNextPlayer;

@Hide
public final class ShiftPlayers extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction player1;
    private final IntFunction player2;
    
    public ShiftPlayers(@And @Or final IntFunction player1, @And @Or final RoleType role1, @And @Or2 final IntFunction player2, @And @Or2 final RoleType role2, @Opt final Then then) {
        super(then);
        this.player1 = ((player1 == null) ? new Id(null, role1) : player1);
        this.player2 = ((player2 == null) ? new Id(null, role2) : player2);
    }
    
    @Override
    public Moves eval(final Context context) {
        final int pid1 = this.player1.eval(context);
        final int pid2 = this.player2.eval(context);
        final Moves moves = new BaseMoves(super.then());
        final ActionSwap actionSwap = new ActionSwap(pid1, pid2);
        actionSwap.setDecision(true);
        final Move swapMove = new Move(actionSwap);
        int swapper = pid1;
        for (int swappee = pid1 + 1; swappee <= context.game().players().count(); ++swappee) {
            swapMove.actions().add(new ActionSwap(swapper, swappee));
            ++swapper;
        }
        swapMove.actions().add(new ActionSetNextPlayer(context.state().mover()));
        swapMove.setMover(context.state().mover());
        moves.moves().add(swapMove);
        return moves;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.player1.gameFlags(game) | this.player2.gameFlags(game) | super.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.player1.preprocess(game);
        this.player2.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "Shift";
    }
}
