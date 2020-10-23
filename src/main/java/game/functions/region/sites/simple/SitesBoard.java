// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.simple;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import topology.Topology;
import util.Context;

@Hide
public final class SitesBoard extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    
    public SitesBoard(@Opt final SiteType elementType) {
        this.precomputedRegion = null;
        this.type = elementType;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final Topology graph = context.topology();
        if (this.type == SiteType.Edge) {
            return new Region(graph.edges());
        }
        final boolean useCells = (this.type != null && this.type.equals(SiteType.Cell)) || (this.type == null && context.game().board().defaultSite() != SiteType.Vertex);
        return new Region(useCells ? graph.cells() : graph.vertices());
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public String toString() {
        return "All()";
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
