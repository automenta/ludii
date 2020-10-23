// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.integer;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastTo;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Radial;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.List;

@Hide
public final class IsPipsMatch extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction siteFn;
    
    public IsPipsMatch(@Opt final IntFunction site) {
        this.siteFn = ((site == null) ? new LastTo(null) : site);
    }
    
    @Override
    public boolean eval(final Context context) {
        final int site = this.siteFn.eval(context);
        final ContainerState cs = context.containerState(context.containerId()[site]);
        final int what = cs.what(site, SiteType.Cell);
        if (what == 0) {
            return true;
        }
        final Component component = context.components()[what];
        final Topology topology = context.topology();
        if (!component.isDomino()) {
            return true;
        }
        final int state = cs.state(site, SiteType.Cell);
        final TIntArrayList locs = component.locs(context, site, state, context.topology());
        final TIntArrayList locsAroundOccupied = new TIntArrayList();
        for (int i = 0; i < locs.size(); ++i) {
            final int loc = locs.getQuick(i);
            final int value = cs.value(loc);
            final Cell cell = context.topology().cells().get(loc);
            final List<Step> steps = topology.trajectories().steps(SiteType.Cell, cell.index(), SiteType.Cell, AbsoluteDirection.Orthogonal);
            for (final Step step : steps) {
                final int to = step.to().id();
                if (!locs.contains(to)) {
                    final int valueTo = cs.value(to);
                    final boolean empty = cs.isEmpty(to, SiteType.Cell);
                    if (!empty && valueTo != value) {
                        return false;
                    }
                    if (!cs.isOccupied(to)) {
                        continue;
                    }
                    locsAroundOccupied.add(to);
                }
            }
        }
        if (context.trial().moveNumber() > 1) {
            boolean okMatch = false;
            for (int j = 0; j < locsAroundOccupied.size(); ++j) {
                final Cell cellI = context.topology().cells().get(locsAroundOccupied.getQuick(j));
                final List<Radial> radials = topology.trajectories().radials(SiteType.Cell, cellI.index(), AbsoluteDirection.Orthogonal);
                final TIntArrayList nbors = new TIntArrayList();
                nbors.add(cellI.index());
                for (final Radial radial : radials) {
                    for (int k = 1; k < radial.steps().length; ++k) {
                        final int to2 = radial.steps()[k].id();
                        if (locsAroundOccupied.contains(to2)) {
                            nbors.add(to2);
                        }
                    }
                    if (nbors.size() > 2) {
                        return false;
                    }
                    if (nbors.size() != 2) {
                        continue;
                    }
                    okMatch = (cs.countCell(nbors.getQuick(0)) == cs.countCell(nbors.getQuick(1)));
                }
            }
            return okMatch;
        }
        return true;
    }
    
    @Override
    public boolean isStatic() {
        final boolean isStatic = this.siteFn.isStatic();
        return isStatic;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long stateFlag = this.siteFn.gameFlags(game) | 0x8000000L | 0x4000L;
        return stateFlag;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.siteFn.preprocess(game);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[MatchPips ");
        sb.append("]");
        return sb.toString();
    }
}
