// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.wip;

import annotations.Hide;
import annotations.Or;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.last.LastTo;
import game.types.board.SiteType;
import game.types.play.RoleType;
import topology.Edge;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.BitSet;

@Hide
public final class CountNonLeavesDegree extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    
    public CountNonLeavesDegree(@Or final IntFunction who, @Or final RoleType role) {
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
        this.who = ((who == null) ? new Id(null, role) : who);
    }
    
    @Override
    public int eval(final Context context) {
        final int siteId = new LastTo(null).eval(context);
        if (siteId == -1) {
            return 0;
        }
        final Topology graph = context.topology();
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int[] localParent = new int[totalVertices];
        final BitSet[] degreeInfo = new BitSet[totalVertices];
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
                if (vARoot == vBRoot) {
                    return 0;
                }
                localParent[vARoot] = vBRoot;
            }
        }
        int sum = 0;
        for (int j = 0; j < totalVertices; ++j) {
            if (degreeInfo[j].cardinality() > 1) {
                sum += degreeInfo[j].cardinality();
            }
        }
        return sum;
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
        return "CountNonLeavesDegree()";
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
