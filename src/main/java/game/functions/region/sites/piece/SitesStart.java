// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.piece;

import annotations.Hide;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.util.equipment.Region;
import game.util.moves.Piece;
import util.Context;

@Hide
public final class SitesStart extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final IntFunction indexFn;
    
    public SitesStart(final Piece piece) {
        this.precomputedRegion = null;
        this.indexFn = piece.component();
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        if (this.indexFn == null) {
            return new Region();
        }
        final int index = this.indexFn.eval(context);
        if (index < 1 || index >= context.components().length) {
            return new Region();
        }
        return new Region(context.trial().startingPos().get(index).toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.indexFn != null && this.indexFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        if (this.indexFn != null) {
            return this.indexFn.gameFlags(game);
        }
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
