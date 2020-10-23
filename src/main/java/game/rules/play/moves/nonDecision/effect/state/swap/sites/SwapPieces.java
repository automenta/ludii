// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.state.swap.sites;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastFrom;
import game.functions.ints.last.LastTo;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.board.SiteType;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.move.ActionAdd;
import util.action.move.ActionMove;
import util.state.containerState.ContainerState;

@Hide
public final class SwapPieces extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction locAFn;
    private final IntFunction locBFn;
    
    public SwapPieces(@Opt final IntFunction locA, @Opt final IntFunction locB, @Opt final Then then) {
        super(then);
        this.locAFn = ((locA == null) ? new LastFrom(null) : locA);
        this.locBFn = ((locB == null) ? new LastTo(null) : locB);
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final int locA = this.locAFn.eval(context);
        final int locB = this.locBFn.eval(context);
        final ContainerState cs = context.containerState(context.containerId()[locB]);
        final int whatB = cs.whatCell(locB);
        final ActionMove actionMove = new ActionMove(SiteType.Cell, locA, -1, SiteType.Cell, locB, -1, -1, -1, false);
        if (this.isDecision()) {
            actionMove.setDecision(true);
        }
        final Move swapMove = new Move(actionMove);
        final Action actionAdd = new ActionAdd(null, locA, whatB, 1, -1, -1, null, null, null);
        swapMove.actions().add(actionAdd);
        swapMove.setFromNonDecision(locA);
        swapMove.setToNonDecision(locB);
        swapMove.setMover(context.state().mover());
        result.moves().add(swapMove);
        if (this.then() != null) {
            for (int j = 0; j < result.moves().size(); ++j) {
                result.moves().get(j).then().add(this.then().moves());
            }
        }
        return result;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0x1L | this.locAFn.gameFlags(game) | this.locBFn.gameFlags(game) | super.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return this.locAFn.isStatic() && this.locBFn.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.locAFn.preprocess(game);
        this.locBFn.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "Swap";
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        return false;
    }
}
