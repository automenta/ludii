// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.index;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import topology.Topology;
import util.Context;

@Hide
public final class SitesColumn extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final IntFunction index;
    
    public SitesColumn(@Opt final SiteType elementType, final IntFunction index) {
        this.precomputedRegion = null;
        this.type = elementType;
        this.index = index;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final SiteType realType = (this.type != null) ? this.type : context.board().defaultSite();
        final Topology graph = context.topology();
        if (this.index == null) {
            return new Region();
        }
        final int i = this.index.eval(context);
        if (i < 0) {
            System.out.println("** Negative column index.");
            return new Region();
        }
        return new Region(graph.columns(realType).get(i));
    }
    
    @Override
    public boolean isStatic() {
        return this.index == null || this.index.isStatic();
    }
    
    @Override
    public String toString() {
        return "Column()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.index != null) {
            flags = this.index.gameFlags(game);
        }
        flags |= SiteType.stateFlags(this.type);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.index != null) {
            this.index.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
