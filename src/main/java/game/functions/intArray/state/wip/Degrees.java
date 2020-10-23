// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.intArray.state.wip;

import annotations.Hide;
import annotations.Or;
import game.Game;
import game.functions.intArray.BaseIntArrayFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.last.LastTo;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.Player;
import topology.Edge;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.BitSet;

@Hide
public final class Degrees extends BaseIntArrayFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    
    public Degrees(@Or final Player who, @Or final RoleType role) {
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
    public int[] eval(final Context context) {
        final int siteId = new LastTo(null).eval(context);
        final Topology graph = context.topology();
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int[] localParent = new int[totalVertices];
        final BitSet[] degreeInfo = new BitSet[totalVertices];
        int totalItems = 0;
        if (whoSiteId == 0) {
            if (state.what(siteId, SiteType.Edge) == 0) {
                whoSiteId = 1;
            }
            else {
                whoSiteId = state.what(siteId, SiteType.Edge);
            }
        }
        for (int i = 0; i < totalVertices; ++i) {
            degreeInfo[localParent[i] = i] = new BitSet(totalEdges);
        }
        for (int k = 0; k < totalEdges; ++k) {
            if (state.what(k, SiteType.Edge) == whoSiteId) {
                final Edge kEdge = graph.edges().get(k);
                final int vA = kEdge.vA().index();
                final int vB = kEdge.vB().index();
                degreeInfo[vA].set(vB);
                degreeInfo[vB].set(vA);
                final int vARoot = this.find(vA, localParent);
                final int vBRoot = this.find(vB, localParent);
                localParent[vARoot] = vBRoot;
                ++totalItems;
            }
        }
        final int[] degreesInfo = new int[totalItems];
        int j = 0;
        for (int l = 0; l < totalVertices; ++l) {
            if (degreeInfo[l].cardinality() >= 1) {
                degreesInfo[j++] = degreeInfo[l].cardinality();
            }
        }
        return degreesInfo;
    }
    
    private int find(final int position, final int[] parent) {
        final int parentId = parent[position];
        if (parentId == position) {
            return position;
        }
        return this.find(parentId, parent);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Degrees()";
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
