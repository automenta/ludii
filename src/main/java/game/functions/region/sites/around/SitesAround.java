// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.around;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.types.board.RegionTypeDynamic;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.equipment.Region;
import game.util.graph.Radial;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.List;

@Hide
public final class SitesAround extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction where;
    private final IntFunction locWhere;
    private final RegionTypeDynamic typeDynamic;
    private final IntFunction distance;
    private final AbsoluteDirection directions;
    private final BooleanFunction cond;
    private final BooleanFunction originIncluded;
    
    public SitesAround(@Opt final SiteType typeLoc, @Or final IntFunction where, @Or final RegionFunction regionWhere, @Opt final RegionTypeDynamic type, @Opt @Name final IntFunction distance, @Opt final AbsoluteDirection directions, @Opt @Name final BooleanFunction If, @Opt @Name final BooleanFunction includeSelf) {
        this.where = regionWhere;
        this.locWhere = where;
        this.typeDynamic = type;
        this.distance = ((distance == null) ? new IntConstant(1) : distance);
        this.directions = ((directions == null) ? AbsoluteDirection.Adjacent : directions);
        this.cond = (If);
        this.originIncluded = ((includeSelf == null) ? BooleanConstant.construct(false) : includeSelf);
        this.type = typeLoc;
    }
    
    @Override
    public Region eval(final Context context) {
        final TIntArrayList sitesAround = new TIntArrayList();
        final int dist = this.distance.eval(context);
        final TIntArrayList typeRegionTo = context.convertRegion(this.typeDynamic);
        final int origFrom = context.from();
        final int origTo = context.to();
        int[] sites;
        if (this.where != null) {
            sites = this.where.eval(context).sites();
        }
        else {
            sites = new int[] { this.locWhere.eval(context) };
        }
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        if (sites.length == 0) {
            return new Region(sitesAround.toArray());
        }
        if (sites[0] == -1) {
            return new Region();
        }
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        for (final int site : sites) {
            if (site < graph.getGraphElements(realType).size()) {
                final List<Radial> radials = graph.trajectories().radials(realType, site, this.directions);
                for (final Radial radial : radials) {
                    for (int toIdx = 0; toIdx < radial.steps().length; ++toIdx) {
                        if (dist < radial.steps().length) {
                            context.setFrom(radial.steps()[0].id());
                            final int to = radial.steps()[dist].id();
                            context.setTo(to);
                            if ((this.cond == null || this.cond.eval(context)) && (this.typeDynamic == null || typeRegionTo.contains(state.who(to, realType))) && !sitesAround.contains(to)) {
                                sitesAround.add(to);
                            }
                        }
                    }
                }
            }
        }
        context.setFrom(origFrom);
        context.setTo(origTo);
        if (this.originIncluded.eval(context)) {
            sitesAround.add(sites[0]);
        }
        return new Region(sitesAround.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return (this.where == null || this.where.isStatic()) && (this.locWhere == null || this.locWhere.isStatic()) && (this.distance == null || this.distance.isStatic()) && (this.originIncluded == null || this.originIncluded.isStatic()) && (this.cond == null || this.cond.isStatic());
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flag = 0L;
        flag |= SiteType.stateFlags(this.type);
        if (this.where != null) {
            flag |= this.where.gameFlags(game);
        }
        if (this.locWhere != null) {
            flag |= this.locWhere.gameFlags(game);
        }
        if (this.distance != null) {
            flag |= this.distance.gameFlags(game);
        }
        if (this.originIncluded != null) {
            flag |= this.originIncluded.gameFlags(game);
        }
        if (this.cond != null) {
            flag |= this.cond.gameFlags(game);
        }
        return flag;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.where != null) {
            this.where.preprocess(game);
        }
        if (this.locWhere != null) {
            this.locWhere.preprocess(game);
        }
        if (this.distance != null) {
            this.distance.preprocess(game);
        }
        if (this.originIncluded != null) {
            this.originIncluded.preprocess(game);
        }
        if (this.cond != null) {
            this.cond.preprocess(game);
        }
    }
    
    public IntFunction locWhere() {
        return this.locWhere;
    }
    
    public RegionFunction where() {
        return this.where;
    }
    
    public RegionTypeDynamic type() {
        return this.typeDynamic;
    }
    
    public IntFunction distance() {
        return this.distance;
    }
    
    public AbsoluteDirection directions() {
        return this.directions;
    }
    
    public BooleanFunction cond() {
        return this.cond;
    }
    
    public BooleanFunction originIncluded() {
        return this.originIncluded;
    }
}
