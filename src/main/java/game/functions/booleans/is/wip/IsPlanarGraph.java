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
import topology.Edge;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.BitSet;

@Hide
public class IsPlanarGraph extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    
    public IsPlanarGraph(@Or final Player who, @Or final RoleType role) {
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
        final int[] parent = new int[totalVertices];
        final BitSet[] adjacencyGraph = new BitSet[totalVertices];
        System.out.println("number of n :" + graph.vertices().size());
        System.out.println("number of e :" + graph.edges().size());
        System.out.println("number of f :" + graph.cells().size());
        for (int i = 0; i < totalVertices; ++i) {
            parent[i] = -1;
            adjacencyGraph[i] = new BitSet(totalVertices);
        }
        for (int k = 0; k < totalEdges; ++k) {
            if (state.what(k, SiteType.Edge) == 1) {
                final Edge kEdge = graph.edges().get(k);
                final int vA = kEdge.vA().index();
                final int vB = kEdge.vB().index();
                adjacencyGraph[vA].set(vB);
                adjacencyGraph[vB].set(vA);
            }
        }
        while (isTwoDegrees(adjacencyGraph, totalVertices)) {
            removeTwoDegrees(adjacencyGraph, totalVertices);
        }
        final BitSet p_id = new BitSet(totalVertices);
        final BitSet[] itemsList = this.countComponents(adjacencyGraph, p_id, totalVertices);
        for (int j = 0; j < totalVertices; ++j) {
            if (itemsList[j].cardinality() >= 5 && p_id.get(j)) {
                final int n = itemsList[j].cardinality();
                int e = 0;
                for (int l = itemsList[j].nextSetBit(0); l >= 0; l = itemsList[j].nextSetBit(l + 1)) {
                    for (int m = adjacencyGraph[l].nextSetBit(0); m >= 0; m = adjacencyGraph[l].nextSetBit(m + 1)) {
                        if (l < m) {
                            ++e;
                        }
                    }
                }
                System.out.println("i: " + j + " n: " + n + " e: " + e);
                if (e >= 7) {
                    if (e > 3 * n - 6) {
                        return false;
                    }
                }
            }
        }
        return false;
    }
    
    private BitSet[] countComponents(final BitSet[] adjacencyGraph, final BitSet p_id, final int totalVertices) {
        final int[] parentId = new int[totalVertices];
        final BitSet[] itemsList = new BitSet[totalVertices];
        for (int i = 0; i < totalVertices; ++i) {
            parentId[i] = -1;
            itemsList[i] = new BitSet(totalVertices);
        }
        for (int i = 0; i < totalVertices; ++i) {
            for (int j = adjacencyGraph[i].nextSetBit(0); j >= 0; j = adjacencyGraph[i].nextSetBit(j + 1)) {
                final int iRoot = this.find(i, parentId);
                final int jRoot = this.find(j, parentId);
                if (parentId[iRoot] == -1) {
                    parentId[iRoot] = iRoot;
                    itemsList[iRoot].set(iRoot);
                }
                if (parentId[jRoot] == -1) {
                    parentId[jRoot] = jRoot;
                    itemsList[jRoot].set(jRoot);
                }
                parentId[iRoot] = jRoot;
                itemsList[jRoot].or(itemsList[iRoot]);
            }
        }
        System.out.println("-------------------------");
        return itemsList;
    }
    
    private static boolean isTwoDegrees(final BitSet[] adjacencyGraph, final int totalVertices) {
        for (int i = 0; i < totalVertices; ++i) {
            if (adjacencyGraph[i].cardinality() == 2) {
                return true;
            }
        }
        return false;
    }
    
    private static void removeTwoDegrees(final BitSet[] adjacencyGraph, final int totalVertices) {
        for (int i = 0; i < totalVertices; ++i) {
            int a = 0;
            int b = 0;
            if (adjacencyGraph[i].cardinality() == 2) {
                a = adjacencyGraph[i].nextSetBit(0);
                for (int j = adjacencyGraph[i].nextSetBit(0); j >= 0; j = adjacencyGraph[i].nextSetBit(j + 1)) {
                    if (j != a) {
                        b = j;
                        break;
                    }
                }
                adjacencyGraph[i].clear(a);
                adjacencyGraph[a].clear(i);
                adjacencyGraph[i].clear(b);
                adjacencyGraph[b].clear(i);
                adjacencyGraph[a].set(b);
                adjacencyGraph[b].set(a);
            }
        }
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
        return "IsPlanarGraph( )";
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
