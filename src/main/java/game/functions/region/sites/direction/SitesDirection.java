// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.direction;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.equipment.Region;
import game.util.graph.Radial;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.ContainerId;
import util.Context;

import java.util.List;

@Hide
public final class SitesDirection extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction regionFn;
    private final BooleanFunction included;
    private final IntFunction distanceFn;
    private final DirectionsFunction dirnChoice;
    private final BooleanFunction stopRule;
    private final BooleanFunction stopIncludedRule;
    
    public SitesDirection(@Or final IntFunction locn, @Or final RegionFunction region, @Opt final Direction directions, @Opt @Name final BooleanFunction included, @Opt @Name final BooleanFunction stop, @Opt @Name final BooleanFunction stopIncluded, @Opt @Name final IntFunction distance, @Opt final SiteType type) {
        this.regionFn = ((region != null) ? region : Sites.construct(new IntFunction[] { locn }));
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
        this.included = ((included == null) ? BooleanConstant.construct(false) : included);
        this.stopRule = ((stop == null) ? BooleanConstant.construct(false) : stop);
        this.type = type;
        this.distanceFn = ((distance == null) ? new IntConstant(1000) : distance);
        this.stopIncludedRule = ((stopIncluded == null) ? BooleanConstant.construct(false) : stopIncluded);
    }
    
    @Override
    public Region eval(final Context context) {
        final int[] region = this.regionFn.eval(context).sites();
        final TIntArrayList sites = new TIntArrayList();
        final int distance = this.distanceFn.eval(context);
        for (final int loc : region) {
            final int cid = new ContainerId(null, null, null, null, new IntConstant(loc)).eval(context);
            final Topology topology = context.containers()[cid].topology();
            if (loc == -1) {
                return new Region(sites.toArray());
            }
            final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
            final TopologyElement element = topology.getGraphElements(realType).get(loc);
            final int originTo = context.to();
            if (this.included.eval(context)) {
                sites.add(loc);
            }
            final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, element, null, null, null, context);
            for (final AbsoluteDirection direction : directions) {
                final List<Radial> radialList = topology.trajectories().radials(this.type, element.index(), direction);
                for (final Radial radial : radialList) {
                    int toIdx = 1;
                    while (toIdx < radial.steps().length && toIdx <= distance) {
                        final int to = radial.steps()[toIdx].id();
                        context.setTo(to);
                        if (this.stopRule.eval(context)) {
                            if (this.stopIncludedRule.eval(context)) {
                                sites.add(to);
                                break;
                            }
                            break;
                        }
                        else {
                            sites.add(to);
                            ++toIdx;
                        }
                    }
                }
            }
            context.setTo(originTo);
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.regionFn.isStatic() && this.included.isStatic() && this.stopRule.isStatic() && this.distanceFn.isStatic() && this.stopIncludedRule.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.regionFn.gameFlags(game) | this.included.gameFlags(game) | this.stopIncludedRule.gameFlags(game) | this.stopRule.gameFlags(game) | this.distanceFn.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.regionFn.preprocess(game);
        this.included.preprocess(game);
        this.stopRule.preprocess(game);
        this.distanceFn.preprocess(game);
        this.stopIncludedRule.preprocess(game);
    }
}
