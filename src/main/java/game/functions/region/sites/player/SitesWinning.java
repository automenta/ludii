// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.player;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.BaseRegionFunction;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.NonDecision;
import game.types.play.RoleType;
import game.util.equipment.Region;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;

@Hide
public final class SitesWinning extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction indexFn;
    private final NonDecision movesGenerator;
    
    public SitesWinning(@Or @Opt final Player player, @Or @Opt final RoleType role, final NonDecision moves) {
        this.indexFn = ((role != null) ? new Id(null, role) : ((player != null) ? player.index() : null));
        this.movesGenerator = moves;
    }
    
    @Override
    public Region eval(final Context context) {
        final TIntArrayList winningPositions = new TIntArrayList();
        if (this.indexFn == null) {
            return new Region();
        }
        final int pid = this.indexFn.eval(context);
        if (pid == context.state().mover()) {
            final int mover = context.state().mover();
            final Moves legalMoves = this.movesGenerator.eval(context);
            for (final Move m : legalMoves.moves()) {
                if (m.toNonDecision() != -1 && !winningPositions.contains(m.toNonDecision())) {
                    final Context newContext = new Context(context);
                    newContext.game().apply(newContext, m);
                    if (!newContext.winners().contains(mover)) {
                        continue;
                    }
                    winningPositions.add(m.toNonDecision());
                    newContext.winners().remove(mover);
                }
            }
        }
        else {
            final Context newContext2 = new Context(context);
            newContext2.setMoverAndImpliedPrevAndNext(pid);
            final Moves legalMoves = this.movesGenerator.eval(newContext2);
            for (final Move m : legalMoves.moves()) {
                if (m.toNonDecision() != -1 && !winningPositions.contains(m.toNonDecision())) {
                    final Context newNewContext = new Context(newContext2);
                    newNewContext.game().apply(newNewContext, m);
                    if (!newNewContext.winners().contains(pid)) {
                        continue;
                    }
                    winningPositions.add(m.toNonDecision());
                    newNewContext.winners().remove(pid);
                }
            }
        }
        return new Region(winningPositions.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = this.movesGenerator.gameFlags(game);
        if (this.indexFn != null) {
            flags |= this.indexFn.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.indexFn != null) {
            this.indexFn.preprocess(game);
        }
        this.movesGenerator.preprocess(game);
    }
}
