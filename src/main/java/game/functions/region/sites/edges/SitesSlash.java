// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.edges;

import annotations.Hide;
import game.Game;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import topology.Topology;
import util.Context;

@Hide
public final class SitesSlash extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    
    public SitesSlash() {
        this.precomputedRegion = null;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final Topology graph = context.topology();
        return new Region(graph.slash(SiteType.Edge));
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public String toString() {
        return "Slash()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 75497472L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.precomputedRegion = this.eval(new Context(game, null));
    }
}
