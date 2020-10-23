// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.wip;

import annotations.Hide;
import annotations.Name;
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
public final class CountTreeDepth extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction rootLocationFn;
    private final IntFunction who;
    
    public CountTreeDepth(@Or final IntFunction who, @Or final RoleType role, @Name final IntFunction root) {
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
        this.rootLocationFn = root;
        this.who = ((who == null) ? new Id(null, role) : who);
    }
    
    @Override
    public int eval(final Context context) {
        final int siteId = new LastTo(null).eval(context);
        if (siteId == -1) {
            return 0;
        }
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int[] localParent = new int[totalVertices];
        final BitSet[] itemsList = new BitSet[totalVertices];
        final BitSet[] adjacencyGraph = new BitSet[totalVertices];
        final int numPlayers = context.game().players().count();
        if (whoSiteId == 0) {
            if (state.what(siteId, SiteType.Edge) == 0) {
                whoSiteId = 1;
            }
            else {
                whoSiteId = state.who(siteId, SiteType.Edge);
            }
        }
        for (int i = 0; i < totalVertices; ++i) {
            localParent[i] = -1;
            itemsList[i] = new BitSet(totalEdges);
            adjacencyGraph[i] = new BitSet(totalEdges);
        }
        for (int k = 0; k < totalEdges; ++k) {
            if ((whoSiteId <= numPlayers && state.who(k, SiteType.Edge) == whoSiteId) || (whoSiteId == numPlayers + 1 && state.who(k, SiteType.Edge) != 0)) {
                final Edge kEdge = graph.edges().get(k);
                final int vA = kEdge.vA().index();
                final int vB = kEdge.vB().index();
                adjacencyGraph[vA].set(vB);
                adjacencyGraph[vB].set(vA);
                final int vARoot = this.find(vA, localParent);
                final int vBRoot = this.find(vB, localParent);
                if (vARoot == vBRoot) {
                    return 0;
                }
                if (localParent[vARoot] == -1) {
                    localParent[vARoot] = vARoot;
                    itemsList[vARoot].set(vARoot);
                }
                if (localParent[vBRoot] == -1) {
                    localParent[vBRoot] = vBRoot;
                    itemsList[vBRoot].set(vBRoot);
                }
                localParent[vARoot] = vBRoot;
                itemsList[vBRoot].or(itemsList[vARoot]);
            }
        }
        final BitSet siteIdList = new BitSet(totalVertices);
        final BitSet subTree = new BitSet(totalVertices);
        final int root = this.rootLocationFn.eval(context);
        siteIdList.set(root);
        for (int j = 0; j < totalVertices; ++j) {
            if (localParent[j] == j && siteIdList.intersects(itemsList[j])) {
                subTree.or(itemsList[j]);
                break;
            }
        }
        return this.depthLimit(root, -1, -1, subTree, adjacencyGraph);
    }
    
    private int depthLimit(final int u, final int index, final int max, final BitSet subTree, final BitSet[] adjacencyGraph) {
        subTree.clear(u);
        final int newIndex = index + 1;
        int newMax = max;
        if (newIndex > newMax) {
            newMax = newIndex;
        }
        if (subTree.cardinality() == 0) {
            return newMax;
        }
        for (int v = adjacencyGraph[u].nextSetBit(0); v >= 0; v = adjacencyGraph[u].nextSetBit(v + 1)) {
            if (subTree.get(v)) {
                return this.depthLimit(v, newIndex, newMax, subTree, adjacencyGraph);
            }
        }
        return newMax;
    }
    
    private int find(final int position, final int[] parent) {
        final int parentId = parent[position];
        if (parentId == position || parentId == -1) {
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
        return "CountTreeDepth()";
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
