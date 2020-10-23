// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.wip;

import annotations.Hide;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Edge;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.BitSet;

@Hide
public class IsPlanarGraph2 extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    
    public IsPlanarGraph2(@Or final Player who, @Or final RoleType role) {
        int numNonNull = 0;
        if (who != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Only one Or parameter must be non-null.");
        }
        this.who = ((who == null) ? new Id(null, role) : who.index());
    }
    
    @Override
    public boolean eval(final Context context) {
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int totalFaces = graph.cells().size();
        final BitSet connectedVeretex = new BitSet(totalVertices);
        final BitSet connectedFaces = new BitSet(totalFaces);
        final BitSet connectedEdges = new BitSet(totalEdges);
        System.out.println("---------------------------");
        System.out.println("number of n :" + graph.vertices().size());
        System.out.println("number of e :" + graph.edges().size());
        System.out.println("number of f :" + graph.cells().size());
        for (int k = 0; k < totalFaces; ++k) {
            final Cell cell = graph.cells().get(k);
            final TIntArrayList boundedEdges = new TIntArrayList();
            for (final Edge edge : cell.edges()) {
                boundedEdges.add(edge.index());
            }
            int coloredEdges = 0;
            System.out.println("k :" + k + " boundedEdges: " + boundedEdges);
            for (int i = 0; i < boundedEdges.size(); ++i) {
                final Edge kEdge = graph.edges().get(boundedEdges.getQuick(i));
                if (state.what(kEdge.index(), SiteType.Edge) == 1) {
                    ++coloredEdges;
                    connectedEdges.set(kEdge.index());
                    connectedVeretex.set(kEdge.vA().index());
                    connectedVeretex.set(kEdge.vB().index());
                }
            }
            System.out.println("here :" + coloredEdges + " edges :" + boundedEdges.size());
            if (coloredEdges == boundedEdges.size()) {
                connectedFaces.set(k);
            }
        }
        System.out.println("number of n :" + connectedVeretex.cardinality());
        System.out.println("number of e :" + connectedEdges.cardinality());
        System.out.println("number of f :" + connectedFaces.cardinality());
        return false;
    }
    
    @Override
    public String toString() {
        return "IsPlanarGraph2( )";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 8388608L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.who.preprocess(game);
    }
}
