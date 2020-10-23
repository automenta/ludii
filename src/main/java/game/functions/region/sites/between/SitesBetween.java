// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.between;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.equipment.Region;
import game.util.graph.Radial;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;

import java.util.List;

@Hide
public final class SitesBetween extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction fromFn;
    private final BooleanFunction fromIncludedFn;
    private final IntFunction toFn;
    private final BooleanFunction toIncludedFn;
    private final BooleanFunction betweenCond;
    private final DirectionsFunction dirnChoice;
    
    public SitesBetween(@Opt final Direction directions, @Opt final SiteType type, @Name final IntFunction from, @Opt @Name final BooleanFunction fromIncluded, @Name final IntFunction to, @Opt @Name final BooleanFunction toIncluded, @Opt @Name final BooleanFunction cond) {
        this.fromFn = from;
        this.toFn = to;
        this.fromIncludedFn = ((fromIncluded == null) ? BooleanConstant.construct(false) : fromIncluded);
        this.toIncludedFn = ((toIncluded == null) ? BooleanConstant.construct(false) : toIncluded);
        this.type = type;
        this.betweenCond = ((cond == null) ? BooleanConstant.construct(true) : cond);
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
    }
    
    @Override
    public Region eval(final Context context) {
        final int from = this.fromFn.eval(context);
        if (from <= -1) {
            return new Region();
        }
        final int to = this.toFn.eval(context);
        if (to <= -1) {
            return new Region();
        }
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        if (from >= topology.getGraphElements(realType).size()) {
            return new Region();
        }
        if (to >= topology.getGraphElements(realType).size()) {
            return new Region();
        }
        final TopologyElement fromV = topology.getGraphElements(realType).get(from);
        final int origFrom = context.from();
        final int origTo = context.to();
        final int origBetween = context.between();
        final TIntArrayList sites = new TIntArrayList();
        context.setFrom(from);
        context.setTo(to);
        if (this.fromIncludedFn.eval(context)) {
            sites.add(from);
        }
        if (this.toIncludedFn.eval(context)) {
            sites.add(to);
        }
        final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, fromV, null, null, null, context);
        boolean toFound = false;
        for (final AbsoluteDirection direction : directions) {
            final List<Radial> radials = topology.trajectories().radials(this.type, fromV.index(), direction);
            for (final Radial radial : radials) {
                context.setBetween(origBetween);
                for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                    final int site = radial.steps()[toIdx].id();
                    if (site == to) {
                        for (int betweenIdx = toIdx - 1; betweenIdx >= 1; --betweenIdx) {
                            final int between = radial.steps()[betweenIdx].id();
                            context.setBetween(between);
                            if (this.betweenCond.eval(context)) {
                                sites.add(between);
                            }
                        }
                        toFound = true;
                        break;
                    }
                }
                if (toFound) {
                    break;
                }
            }
            if (toFound) {
                break;
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        context.setBetween(origBetween);
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.fromFn.isStatic() && this.fromIncludedFn.isStatic() && this.toFn.isStatic() && this.toIncludedFn.isStatic() && this.betweenCond.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        flags |= SiteType.stateFlags(this.type);
        flags |= this.fromFn.gameFlags(game);
        flags |= this.toFn.gameFlags(game);
        flags |= this.fromIncludedFn.gameFlags(game);
        flags |= this.toIncludedFn.gameFlags(game);
        flags |= this.betweenCond.gameFlags(game);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.fromFn.preprocess(game);
        this.toFn.preprocess(game);
        this.fromIncludedFn.preprocess(game);
        this.toIncludedFn.preprocess(game);
        this.betweenCond.preprocess(game);
    }
}
