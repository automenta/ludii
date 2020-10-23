// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.wip;

import annotations.Hide;
import game.Game;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Edge;
import topology.Topology;
import topology.Vertex;
import util.ContainerId;
import util.Context;
import util.state.puzzleState.ContainerDeductionPuzzleState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Hide
public class BorderSites extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction location;
    
    public BorderSites(final IntFunction loc) {
        this.location = loc;
    }
    
    @Override
    public Region eval(final Context context) {
        final ContainerDeductionPuzzleState ps = (ContainerDeductionPuzzleState)context.state().containerStates()[0];
        final int loc = this.location.eval(context);
        final int cid = new ContainerId(null, null, null, null, new IntConstant(loc)).eval(context);
        final Topology graph = context.containers()[cid].topology();
        final TIntArrayList sites = new TIntArrayList();
        final List<Cell> allFaces = graph.cells();
        final Queue<Cell> queue = new LinkedList<>();
        sites.add(loc);
        queue.add(graph.cells().get(loc));
        while (!queue.isEmpty()) {
            final Cell currentFace = queue.remove();
            final List<Vertex> neighbourVertices = currentFace.vertices();
            final List<Edge> neighbourEdges = new ArrayList<>();
            for (final Edge edge : graph.edges()) {
                if (!neighbourEdges.contains(edge) && neighbourVertices.contains(edge.vA()) && neighbourVertices.contains(edge.vB())) {
                    neighbourEdges.add(edge);
                }
            }
            for (final Edge e : neighbourEdges) {
                if (ps.isResolvedEdges(e.index()) && ps.whatEdge(e.index()) == 0) {
                    for (final Cell newFace : allFaces) {
                        if (!sites.contains(newFace.index()) && currentFace.index() != newFace.index() && newFace.vertices().contains(e.vA()) && newFace.vertices().contains(e.vB())) {
                            sites.add(newFace.index());
                            queue.add(newFace);
                            break;
                        }
                    }
                }
            }
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.location.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.location.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.location.preprocess(game);
    }
}
