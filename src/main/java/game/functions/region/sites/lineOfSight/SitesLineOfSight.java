// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.lineOfSight;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.last.Last;
import game.functions.ints.last.LastType;
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
import util.state.containerState.ContainerState;

import java.util.List;

@Hide
public final class SitesLineOfSight extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final LineOfSightType typeLoS;
    private final IntFunction loc;
    private final DirectionsFunction dirnChoice;
    private SiteType typeLoc;
    
    public SitesLineOfSight(@Opt final LineOfSightType typeLoS, @Opt final SiteType typeLoc, @Opt @Name final IntFunction at, @Opt final Direction directions) {
        this.typeLoS = ((typeLoS == null) ? LineOfSightType.Piece : typeLoS);
        this.loc = ((at == null) ? Last.construct(LastType.To, null) : at);
        this.typeLoc = typeLoc;
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
    }
    
    @Override
    public Region eval(final Context context) {
        final TIntArrayList sitesLineOfSight = new TIntArrayList();
        final int from = this.loc.eval(context);
        if (from == -1) {
            return new Region(sitesLineOfSight.toArray());
        }
        final ContainerState cs = context.containerState(context.containerId()[from]);
        final Topology graph = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement fromV = graph.getGraphElements(realType).get(from);
        final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, fromV, null, null, null, context);
        for (final AbsoluteDirection direction : directions) {
            final List<Radial> radials = graph.trajectories().radials(realType, fromV.index(), direction);
            for (final Radial radial : radials) {
                int prevTo = -1;
                for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                    final int to = radial.steps()[toIdx].id();
                    final int what = cs.what(to, realType);
                    switch (this.typeLoS) {
                        case Empty -> {
                            if (what == 0) {
                                sitesLineOfSight.add(to);
                            }
                        }
                        case Farthest -> {
                            if (what != 0 && prevTo != -1) {
                                sitesLineOfSight.add(prevTo);
                            }
                            if (toIdx == radial.steps().length - 1) {
                                sitesLineOfSight.add(to);
                            }
                        }
                        case Piece -> {
                            if (what != 0) {
                                sitesLineOfSight.add(to);
                            }
                        }
                        default -> System.out.println("** SitesLineOfSight(): Should never reach here.");
                    }
                    if (what != 0) {
                        break;
                    }
                    prevTo = to;
                }
            }
        }
        return new Region(sitesLineOfSight.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flag = this.loc.gameFlags(game);
        if (this.type != null) {
            if (this.type == SiteType.Edge || this.type == SiteType.Vertex) {
                flag |= 0x800000L;
            }
            if (this.type == SiteType.Edge) {
                flag |= 0x4000000L;
            }
            if (this.type == SiteType.Vertex) {
                flag |= 0x1000000L;
            }
            if (this.type == SiteType.Cell) {
                flag |= 0x2000000L;
            }
        }
        return flag;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.typeLoc == null) {
            this.typeLoc = game.board().defaultSite();
        }
        this.loc.preprocess(game);
    }
}
