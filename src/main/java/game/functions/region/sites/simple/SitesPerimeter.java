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
public final class SitesPerimeter extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    
    public SitesPerimeter(@Opt final SiteType elementType) {
        this.precomputedRegion = null;
        this.type = elementType;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
        final Topology graph = context.topology();
        return new Region(graph.perimeter(realType));
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public String toString() {
        return "Outer()";
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
