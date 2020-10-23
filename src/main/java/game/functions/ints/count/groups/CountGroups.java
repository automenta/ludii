// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.groups;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.board.SiteType;
import game.types.play.RoleType;
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
public final class CountGroups extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private SiteType type;
    private final IntFunction whoFn;
    private final IntFunction minFn;
    private final BooleanFunction condition;
    private final DirectionsFunction dirnChoice;
    private final boolean allPieces;
    
    public CountGroups(@Opt final SiteType type, @Opt final Direction directions, @Opt @Or final RoleType role, @Opt @Or @Name final IntFunction of, @Opt @Or @Name final BooleanFunction If, @Opt @Name final IntFunction min) {
        this.type = type;
        this.whoFn = ((of != null) ? of : ((role != null) ? new Id(null, role) : new Id(null, RoleType.All)));
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
        this.minFn = ((min == null) ? new IntConstant(0) : min);
        this.condition = If;
        this.allPieces = ((If == null && of == null && role == null) || (role != null && (role.equals(RoleType.All) || role.equals(RoleType.Shared))));
    }
    
    @Override
    public int eval(final Context context) {
        final Topology topology = context.topology();
        final int maxIndexElement = context.topology().getGraphElements(this.type).size();
        final ContainerState cs = context.containerState(0);
        final int origFrom = context.from();
        final int origTo = context.to();
        final int who = this.whoFn.eval(context);
        final int min = this.minFn.eval(context);
        int count = 0;
        final TIntArrayList sitesChecked = new TIntArrayList();
        final TIntArrayList sitesToCheck = new TIntArrayList();
        if (this.allPieces) {
            for (int i = 1; i < context.game().players().size(); ++i) {
                final TIntArrayList allSites = context.state().owned().sites(i);
                for (int j = 0; j < allSites.size(); ++j) {
                    final int site = allSites.get(j);
                    if (site < maxIndexElement) {
                        sitesToCheck.add(site);
                    }
                }
            }
        }
        else {
            for (int k = 0; k < context.state().owned().sites(who).size(); ++k) {
                final int site2 = context.state().owned().sites(who).get(k);
                if (site2 < maxIndexElement) {
                    sitesToCheck.add(site2);
                }
            }
        }
        for (int l = 0; l < sitesToCheck.size(); ++l) {
            final int from = sitesToCheck.get(l);
            if (!sitesChecked.contains(from)) {
                final TIntArrayList groupSites = new TIntArrayList();
                context.setFrom(from);
                if ((who == cs.who(from, this.type) && this.condition == null) || (this.condition != null && this.condition.eval(context))) {
                    groupSites.add(from);
                }
                else if (this.allPieces && cs.what(from, this.type) != 0) {
                    groupSites.add(from);
                }
                if (groupSites.size() > 0) {
                    context.setFrom(from);
                    final TIntArrayList sitesExplored = new TIntArrayList();
                    int m = 0;
                    while (sitesExplored.size() != groupSites.size()) {
                        final int site3 = groupSites.get(m);
                        final TopologyElement siteElement = topology.getGraphElements(this.type).get(site3);
                        final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(this.type, siteElement, null, null, null, context);
                        for (final AbsoluteDirection direction : directions) {
                            final List<Step> steps = topology.trajectories().steps(this.type, siteElement.index(), this.type, direction);
                            for (final Step step : steps) {
                                final int to = step.to().id();
                                if (groupSites.contains(to)) {
                                    continue;
                                }
                                context.setTo(to);
                                if ((this.condition == null && who == cs.who(to, this.type)) || (this.condition != null && this.condition.eval(context))) {
                                    groupSites.add(to);
                                }
                                else {
                                    if (!this.allPieces || cs.what(to, this.type) == 0) {
                                        continue;
                                    }
                                    groupSites.add(to);
                                }
                            }
                        }
                        sitesExplored.add(site3);
                        ++m;
                    }
                    if (groupSites.size() >= min) {
                        ++count;
                    }
                    sitesChecked.addAll(groupSites);
                }
            }
        }
        context.setTo(origTo);
        context.setFrom(origFrom);
        return count;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Groups()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.whoFn.gameFlags(game) | this.minFn.gameFlags(game);
        if (this.condition != null) {
            gameFlags |= this.condition.gameFlags(game);
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.whoFn.preprocess(game);
        this.minFn.preprocess(game);
        if (this.condition != null) {
            this.condition.preprocess(game);
        }
    }
}
