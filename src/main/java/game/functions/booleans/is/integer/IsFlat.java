// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.integer;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.context.To;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Step;
import topology.Topology;
import topology.Vertex;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.List;

@Hide
public final class IsFlat extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction siteFn;
    
    public IsFlat(@Opt final IntFunction site) {
        this.siteFn = ((site == null) ? To.instance() : site);
    }
    
    @Override
    public boolean eval(final Context context) {
        final int site = this.siteFn.eval(context);
        if (site == -1 && site >= context.topology().vertices().size()) {
            return false;
        }
        final Vertex v = context.topology().vertices().get(site);
        final Topology topology = context.topology();
        if (v.layer() == 0) {
            return true;
        }
        final ContainerState cs = context.containerState(context.containerId()[site]);
        final List<Step> steps = topology.trajectories().steps(SiteType.Vertex, site, SiteType.Vertex, AbsoluteDirection.Downward);
        for (final Step step : steps) {
            if (cs.what(step.to().id(), SiteType.Vertex) == 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "IsFlat()";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
