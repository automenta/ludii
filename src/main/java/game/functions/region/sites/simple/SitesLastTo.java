// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.simple;

import annotations.Hide;
import game.Game;
import game.functions.region.BaseRegionFunction;
import game.rules.play.moves.Moves;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.action.Action;

@Hide
public final class SitesLastTo extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public Region eval(final Context context) {
        final Move lastMove = context.trial().lastMove();
        final TIntArrayList allToMove = new TIntArrayList();
        allToMove.add(lastMove.toNonDecision());
        for (final Action action : lastMove.actions()) {
            final int to = action.to();
            if (!allToMove.contains(to) && to < context.board().numSites() && to >= 0) {
                allToMove.add(to);
            }
        }
        for (final Moves moves : lastMove.then()) {
            moves.eval(context);
            for (final Move m : moves.moves()) {
                final int to2 = m.toNonDecision();
                if (!allToMove.contains(to2) && to2 >= 0) {
                    allToMove.add(to2);
                }
            }
        }
        return new Region(allToMove.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
