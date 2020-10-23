// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle.is.wip;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.types.board.RegionTypeStatic;
import gnu.trove.list.array.TIntArrayList;
import topology.Edge;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public class AllConnected extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionTypeStatic typeRegion;
    
    public AllConnected(final RegionTypeStatic typeRegion) {
        this.typeRegion = typeRegion;
    }
    
    @Override
    public boolean eval(final Context context) {
        final ContainerState ps = context.state().containerStates()[0];
        final int numEdge = context.board().topology().edges().size();
        if (this.typeRegion == RegionTypeStatic.Vertices) {
            for (int i = 0; i < numEdge; ++i) {
                if (!ps.isResolvedEdges(i)) {
                    return true;
                }
            }
            final TIntArrayList varsConstraint = context.game().constraintVariables();
            final TIntArrayList path = new TIntArrayList();
            final Topology graph = context.topology();
            path.add(0);
            for (int j = 0; j < path.size(); ++j) {
                final int var = path.getQuick(j);
                for (int k = 0; k < numEdge; ++k) {
                    final Edge edge = graph.edges().get(k);
                    if (edge.containsVertex(var) && ps.whatEdge(k) != 0) {
                        if (!path.contains(edge.vA().index())) {
                            path.add(edge.vA().index());
                        }
                        if (!path.contains(edge.vB().index())) {
                            path.add(edge.vB().index());
                        }
                    }
                }
            }
            for (int index = 0; index < varsConstraint.size(); ++index) {
                final int var2 = varsConstraint.getQuick(index);
                if (!path.contains(var2)) {
                    for (final Edge edge2 : graph.edges()) {
                        if (edge2.containsVertex(var2)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        String str = "";
        str += "AllConnected()";
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = 128L;
        return 128L;
    }
}
