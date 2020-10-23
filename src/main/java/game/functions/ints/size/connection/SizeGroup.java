// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.size.connection;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.List;

@Hide
public final class SizeGroup extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final BooleanFunction condition;
    private final DirectionsFunction dirnChoice;
    private SiteType type;
    
    public SizeGroup(@Opt final SiteType type, @Name final IntFunction at, @Opt final Direction directions, @Opt @Name final BooleanFunction If) {
        this.startLocationFn = at;
        this.type = type;
        this.condition = If;
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
    }
    
    @Override
    public int eval(final Context context) {
        final Topology topology = context.topology();
        final int from = this.startLocationFn.eval(context);
        final ContainerState cs = context.containerState(0);
        final int origFrom = context.from();
        final int origTo = context.to();
        final TIntArrayList groupSites = new TIntArrayList();
        context.setTo(from);
        if (this.condition == null || this.condition.eval(context)) {
            groupSites.add(from);
        }
        final int what = cs.what(from, this.type);
        if (!groupSites.isEmpty()) {
            context.setFrom(from);
            final TIntArrayList sitesExplored = new TIntArrayList();
            int i = 0;
            while (sitesExplored.size() != groupSites.size()) {
                final int site = groupSites.get(i);
                final TopologyElement siteElement = topology.getGraphElements(this.type).get(site);
                final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(this.type, siteElement, null, null, null, context);
                for (final AbsoluteDirection direction : directions) {
                    final List<Step> steps = topology.trajectories().steps(this.type, siteElement.index(), this.type, direction);
                    for (final Step step : steps) {
                        final int to = step.to().id();
                        if (groupSites.contains(to)) {
                            continue;
                        }
                        context.setTo(to);
                        if ((this.condition != null || what != cs.what(to, this.type)) && (this.condition == null || !this.condition.eval(context))) {
                            continue;
                        }
                        groupSites.add(to);
                    }
                }
                sitesExplored.add(site);
                ++i;
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        return groupSites.size();
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= this.startLocationFn.gameFlags(game);
        if (this.condition != null) {
            gameFlags |= this.condition.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.condition != null) {
            this.condition.preprocess(game);
        }
        this.startLocationFn.preprocess(game);
    }
}
