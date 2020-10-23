// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.tree;

import annotations.Hide;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
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
public class IsTreeCentre extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    
    public IsTreeCentre(@Or final Player who, @Or final RoleType role) {
        this.who = ((role != null) ? new Id(null, role) : who.index());
    }
    
    @Override
    public boolean eval(final Context context) {
        final int siteId = new LastTo(null).eval(context);
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = this.who.eval(context);
        final int numPlayers = context.game().players().count();
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int[] localParent = new int[totalVertices];
        final BitSet[] itemsList = new BitSet[totalVertices];
        final BitSet[] adjacencyGraph = new BitSet[totalVertices];
        for (int i = 0; i < totalVertices; ++i) {
            localParent[i] = -1;
            itemsList[i] = new BitSet(totalEdges);
            adjacencyGraph[i] = new BitSet(totalEdges);
        }
        for (int k = 0; k < totalEdges; ++k) {
            if ((whoSiteId == numPlayers + 1 && state.what(k, SiteType.Edge) != 0) || (whoSiteId < numPlayers + 1 && state.who(k, SiteType.Edge) == whoSiteId)) {
                final Edge kEdge = graph.edges().get(k);
                final int vA = kEdge.vA().index();
                final int vB = kEdge.vB().index();
                adjacencyGraph[vA].set(vB);
                adjacencyGraph[vB].set(vA);
                final int vARoot = this.find(vA, localParent);
                final int vBRoot = this.find(vB, localParent);
                if (vARoot == vBRoot) {
                    return false;
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
        int centre = 0;
        final BitSet subTree = new BitSet(totalVertices);
        siteIdList.set(siteId);
        for (int j = 0; j < totalVertices; ++j) {
            if (localParent[j] == j && siteIdList.intersects(itemsList[j])) {
                centre = this.build(itemsList[j].nextSetBit(0), -1, adjacencyGraph, totalVertices);
                subTree.or(itemsList[j]);
                break;
            }
        }
        if (siteId == centre) {
            return true;
        }
        if (adjacencyGraph[centre].cardinality() == adjacencyGraph[siteId].cardinality() && adjacencyGraph[centre].cardinality() == 1) {
            return true;
        }
        boolean adjcentVertices = false;
        for (int l = 0; l < totalEdges; ++l) {
            if ((whoSiteId == numPlayers + 1 && state.what(l, SiteType.Edge) != 0) || (whoSiteId < numPlayers + 1 && state.who(l, SiteType.Edge) == whoSiteId)) {
                final Edge kEdge2 = graph.edges().get(l);
                final int vA2 = kEdge2.vA().index();
                final int vB2 = kEdge2.vB().index();
                if ((vA2 == siteId && vB2 == centre) || (vB2 == siteId && vA2 == centre)) {
                    adjcentVertices = true;
                    break;
                }
            }
        }
        if (adjcentVertices) {
            final BitSet subTree2 = (BitSet)subTree.clone();
            final int level1 = this.depthLimit(siteId, 0, 0, subTree2, adjacencyGraph);
            final BitSet subTree3 = (BitSet)subTree.clone();
            final int level2 = this.depthLimit(centre, 0, 0, subTree3, adjacencyGraph);
            return level1 == level2;
        }
        return false;
    }
    
    private int build(final int u, final int parent, final BitSet[] adjacencyGraph, final int totalVertices) {
        final BitSet[] sub = new BitSet[totalVertices];
        final BitSet n = this.dfs_subTreesGenerator(u, parent, adjacencyGraph, sub, totalVertices);
        return this.dfsCenter(u, parent, n.cardinality(), adjacencyGraph, sub);
    }
    
    private BitSet dfs_subTreesGenerator(final int u, final int parent, final BitSet[] adjacenceGraph, final BitSet[] sub, final int totalVertices) {
        (sub[u] = new BitSet(totalVertices)).set(u);
        for (int v = adjacenceGraph[u].nextSetBit(0); v >= 0; v = adjacenceGraph[u].nextSetBit(v + 1)) {
            if (u != v && v != parent) {
                sub[u].or(this.dfs_subTreesGenerator(v, u, adjacenceGraph, sub, totalVertices));
            }
        }
        return sub[u];
    }
    
    private int dfsCenter(final int u, final int parent, final int totalItems, final BitSet[] adjacencyGraph, final BitSet[] sub) {
        for (int v = adjacencyGraph[u].nextSetBit(0); v >= 0; v = adjacencyGraph[u].nextSetBit(v + 1)) {
            if (u != v && v != parent && sub[v].cardinality() > totalItems / 2) {
                return this.dfsCenter(v, u, totalItems, adjacencyGraph, sub);
            }
        }
        return u;
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
    public String toString() {
        return "IsTreeCenter( )";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0x800000L | this.who.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.who.preprocess(game);
    }
}
