// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.math;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.equipment.Region;
import topology.Topology;
import util.ContainerId;
import util.Context;

public final class Expand extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final ContainerId containerId;
    private final RegionFunction baseRegion;
    private final IntFunction numSteps;
    private final AbsoluteDirection direction;
    private Region precomputedRegion;
    
    public Expand(@Opt @Or final IntFunction containerIdFn, @Opt @Or final String containerName, @Or2 final RegionFunction region, @Or2 @Name final IntFunction origin, @Opt @Name final IntFunction steps, @Opt final AbsoluteDirection dirn, @Opt final SiteType type) {
        this.precomputedRegion = null;
        int numNonNull = 0;
        if (containerIdFn != null) {
            ++numNonNull;
        }
        if (containerName != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter must be non-null.");
        }
        numNonNull = 0;
        if (region != null) {
            ++numNonNull;
        }
        if (origin != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or2 parameter must be non-null.");
        }
        this.containerId = new ContainerId(containerIdFn, containerName, null, null, null);
        this.baseRegion = ((region != null) ? region : Sites.construct(new IntFunction[] { origin }));
        this.numSteps = ((steps == null) ? new IntConstant(1) : steps);
        this.direction = dirn;
        this.type = type;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final int cid = this.containerId.eval(context);
        final Region region = new Region(this.baseRegion.eval(context));
        final int num = this.numSteps.eval(context);
        if (num > 0) {
            final Topology graph = context.containers()[cid].topology();
            if (this.direction == null) {
                Region.expand(region, graph, num, (this.type != null) ? this.type : context.board().defaultSite());
            }
            else {
                Region.expand(region, graph, num, context, this.direction, (this.type != null) ? this.type : context.board().defaultSite());
            }
        }
        return region;
    }
    
    @Override
    public boolean isStatic() {
        return this.baseRegion.isStatic() && this.numSteps.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = this.baseRegion.gameFlags(game) | this.numSteps.gameFlags(game);
        if (this.type != null && (this.type == SiteType.Edge || this.type == SiteType.Vertex)) {
            flags |= 0x800000L;
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.baseRegion.preprocess(game);
        this.numSteps.preprocess(game);
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
