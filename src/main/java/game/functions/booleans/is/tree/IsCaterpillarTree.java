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
public class IsCaterpillarTree extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    
    public IsCaterpillarTree(@Or final Player who, @Or final RoleType role) {
        this.who = ((role != null) ? new Id(null, role) : who.index());
    }
    
    @Override
    public boolean eval(final Context context) {
        final int siteId = new LastTo(null).eval(context);
        if (siteId == -1) {
            return false;
        }
        final Topology graph = context.topology();
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int[] localParent = new int[totalVertices];
        int totalExistingedges = 0;
        if (whoSiteId == 0) {
            if (state.what(siteId, SiteType.Edge) == 0) {
                whoSiteId = 1;
            }
            else {
                whoSiteId = state.what(siteId, SiteType.Edge);
            }
        }
        for (int i = 0; i < totalVertices; ++i) {
            localParent[i] = i;
        }
        for (int k = 0; k < totalEdges; ++k) {
            if (state.what(k, SiteType.Edge) == whoSiteId) {
                final Edge kEdge = graph.edges().get(k);
                final int vARoot = this.find(kEdge.vA().index(), localParent);
                final int vBRoot = this.find(kEdge.vB().index(), localParent);
                if (vARoot == vBRoot) {
                    return false;
                }
                localParent[vARoot] = vBRoot;
                ++totalExistingedges;
            }
        }
        if (totalExistingedges != totalVertices - 1) {
            return false;
        }
        int count = 0;
        for (int j = 0; j < totalVertices; ++j) {
            if (localParent[j] == j) {
                ++count;
            }
        }
        if (count != 1) {
            return false;
        }
        final BitSet caterpillarBackbone = new BitSet(totalEdges);
        for (int l = 0; l < totalEdges; ++l) {
            final Edge kEdge2 = graph.edges().get(l);
            if (state.what(l, SiteType.Edge) == whoSiteId) {
                final int kEdgevA = kEdge2.vA().index();
                int degree1 = 0;
                for (int ka = 0; ka < totalEdges; ++ka) {
                    final Edge kaEdge = graph.edges().get(ka);
                    if (state.what(ka, SiteType.Edge) == whoSiteId && (kEdgevA == kaEdge.vA().index() || kEdgevA == kaEdge.vB().index()) && ++degree1 > 1) {
                        break;
                    }
                }
                if (degree1 >= 2) {
                    final int kEdgevB = kEdge2.vB().index();
                    int degree2 = 0;
                    for (int kb = 0; kb < totalEdges; ++kb) {
                        final Edge kbEdge = graph.edges().get(kb);
                        if (state.what(kb, SiteType.Edge) == whoSiteId && (kEdgevB == kbEdge.vA().index() || kEdgevB == kbEdge.vB().index()) && ++degree2 > 1) {
                            break;
                        }
                    }
                    if (degree1 > 1 && degree2 > 1) {
                        caterpillarBackbone.set(kEdge2.index());
                    }
                }
            }
        }
        final Edge kEdge3 = graph.edges().get(caterpillarBackbone.nextSetBit(0));
        final int v1 = kEdge3.vA().index();
        final int v2 = kEdge3.vB().index();
        final BitSet depthBitset1 = new BitSet(totalVertices);
        final BitSet depthBitset2 = new BitSet(totalVertices);
        final BitSet visitedEdge = new BitSet(totalEdges);
        final int componentSz = totalVertices;
        this.dfsMinPathEdge(context, graph, kEdge3, caterpillarBackbone, visitedEdge, 0, v1, v2, componentSz, depthBitset1, whoSiteId);
        this.dfsMinPathEdge(context, graph, kEdge3, caterpillarBackbone, visitedEdge, 0, v2, v1, componentSz, depthBitset2, whoSiteId);
        final int pathLength = depthBitset1.cardinality() - 1 + (depthBitset2.cardinality() - 1) + 1;
        return pathLength == caterpillarBackbone.cardinality();
    }
    
    private int dfsMinPathEdge(final Context context, final Topology graph, final Edge kEdge, final BitSet edgeBitset, final BitSet visitedEdge, final int index, final int presentVertex, final int parent, final int mincomponentsz, final BitSet depthBitset, final int whoSiteId) {
        if (index == mincomponentsz * 2) {
            return index;
        }
        for (int i = edgeBitset.nextSetBit(0); i >= 0; i = edgeBitset.nextSetBit(i + 1)) {
            final Edge nEdge = graph.edges().get(i);
            if (nEdge != kEdge) {
                final int nVA = nEdge.vA().index();
                final int nVB = nEdge.vB().index();
                if (nVA == presentVertex) {
                    visitedEdge.set(i);
                    this.dfsMinPathEdge(context, graph, nEdge, edgeBitset, visitedEdge, index + 1, nVB, nVA, mincomponentsz, depthBitset, whoSiteId);
                }
                else if (nVB == presentVertex) {
                    visitedEdge.set(i);
                    this.dfsMinPathEdge(context, graph, nEdge, edgeBitset, visitedEdge, index + 1, nVA, nVB, mincomponentsz, depthBitset, whoSiteId);
                }
            }
        }
        depthBitset.set(index);
        return index;
    }
    
    private int find(final int position, final int[] parent) {
        final int parentId = parent[position];
        if (parentId == position) {
            return position;
        }
        return this.find(parentId, parent);
    }
    
    @Override
    public String toString() {
        return "IsCaterpillarTree( )";
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
