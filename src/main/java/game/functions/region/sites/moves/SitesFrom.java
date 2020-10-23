// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.moves;

import annotations.Hide;
import game.Game;
import game.functions.region.BaseRegionFunction;
import game.rules.play.moves.Moves;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;

@Hide
public final class SitesFrom extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final Moves moves;
    
    public SitesFrom(final Moves moves) {
        this.moves = moves;
    }
    
    @Override
    public Region eval(final Context context) {
        final TIntArrayList sites = new TIntArrayList();
        final Moves generatedMoves = this.moves.eval(context);
        for (final Move m : generatedMoves.moves()) {
            sites.add(m.fromNonDecision());
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.moves.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.moves.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.moves.preprocess(game);
    }
}
