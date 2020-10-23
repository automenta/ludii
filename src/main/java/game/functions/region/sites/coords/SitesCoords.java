// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.coords;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import topology.SiteFinder;
import topology.TopologyElement;
import util.Context;

@Hide
public final class SitesCoords extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final String[] coords;
    
    public SitesCoords(@Opt final SiteType elementType, final String[] coords) {
        this.precomputedRegion = null;
        this.type = elementType;
        this.coords = coords;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final TIntArrayList sites = new TIntArrayList();
        for (final String coord : this.coords) {
            final TopologyElement element = SiteFinder.find(context.board(), coord, this.type);
            if (element != null) {
                sites.add(element.index());
            }
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public String toString() {
        return "Column()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        flags |= SiteType.stateFlags(this.type);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.precomputedRegion = this.eval(new Context(game, null));
    }
}
