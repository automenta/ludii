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
public final class SitesLastFrom extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public Region eval(final Context context) {
        final Move lastMove = context.trial().lastMove();
        final TIntArrayList allFromMove = new TIntArrayList();
        allFromMove.add(lastMove.fromNonDecision());
        for (final Action action : lastMove.actions()) {
            final int from = action.from();
            if (!allFromMove.contains(from) && from < context.board().numSites() && from >= 0) {
                allFromMove.add(from);
            }
        }
        for (final Moves moves : lastMove.then()) {
            moves.eval(context);
            for (final Move m : moves.moves()) {
                final int from2 = m.fromNonDecision();
                if (!allFromMove.contains(from2) && from2 >= 0) {
                    allFromMove.add(from2);
                }
            }
        }
        return new Region(allFromMove.toArray());
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
