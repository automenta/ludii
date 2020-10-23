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
import topology.Cell;
import topology.Topology;
import util.Context;

@Hide
public final class SitesCell extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final IntFunction index;
    
    public SitesCell(@Opt final SiteType elementType, final IntFunction index) {
        this.precomputedRegion = null;
        this.type = elementType;
        this.index = index;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final Topology graph = context.topology();
        if (this.index == null) {
            return new Region();
        }
        final int i = this.index.eval(context);
        if (i < 0 || i >= graph.cells().size()) {
            System.out.println("** Invalid cell index " + i + ".");
            return new Region();
        }
        final Cell cell = graph.cells().get(i);
        return new Region(cell.vertices());
    }
    
    @Override
    public boolean isStatic() {
        return this.index == null || this.index.isStatic();
    }
    
    @Override
    public String toString() {
        return "Cell()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.index != null) {
            flags = this.index.gameFlags(game);
        }
        flags |= 0x3000000L;
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
