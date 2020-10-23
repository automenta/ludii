// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.wip;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import main.Constants;
import topology.Edge;
import topology.Topology;
import topology.Vertex;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.*;

@Hide
public final class SitesPath extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final SiteType resultType;
    private final SiteType ofType;
    private final IntFunction sourceFn;
    private final IntFunction destinationFn;
    private final boolean shortestFlag;
    private final IntFunction who;
    
    public SitesPath(final SiteType resultType, @Name final SiteType of, @Name final IntFunction from, @Name final IntFunction to, @Opt @Name final Boolean shortest, @Opt final Player who) {
        this.resultType = resultType;
        this.ofType = of;
        this.sourceFn = from;
        this.destinationFn = to;
        this.shortestFlag = (shortest != null && shortest);
        this.who = who.index();
    }
    
    @Override
    public Region eval(final Context context) {
        switch (this.ofType) {
            case Vertex -> {
                return this.evalVertex(context);
            }
            case Edge -> {
                return this.evalEdge(context);
            }
            default -> {
                return null;
            }
        }
    }
    
    private Region evalVertex(final Context context) {
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int numPlayers = context.game().players().count();
        final int[] localParent = new int[totalVertices];
        final BitSet[] itemsList = new BitSet[totalVertices];
        final BitSet[] adjacencyGraph = new BitSet[totalVertices];
        final int source = this.sourceFn.eval(context);
        final int destination = this.destinationFn.eval(context);
        for (int i = 0; i < totalVertices; ++i) {
            localParent[i] = Constants.UNUSED;
            itemsList[i] = new BitSet(totalEdges);
            adjacencyGraph[i] = new BitSet(totalEdges);
        }
        for (int k = 0; k < totalVertices; ++k) {
            if ((whoSiteId == numPlayers + 1 && state.what(k, SiteType.Vertex) != 0) || (whoSiteId < numPlayers + 1 && state.who(k, SiteType.Vertex) == whoSiteId)) {
                final Vertex kVertex = graph.vertices().get(k);
                final List<Vertex> nebVertices = kVertex.adjacent();
                for (final topology.Vertex nb : nebVertices) {
                    if (state.who(nb.index(), SiteType.Vertex) == whoSiteId || state.who(nb.index(), SiteType.Vertex) == numPlayers + 1) {
                        adjacencyGraph[k].set(nb.index());
                        adjacencyGraph[nb.index()].set(k);
                        final int vARoot = this.find(k, localParent);
                        final int vBRoot = this.find(nb.index(), localParent);
                        if (vARoot != vBRoot) {
                            if (localParent[vARoot] == Constants.UNUSED) {
                                localParent[vARoot] = vARoot;
                                itemsList[vARoot].set(vARoot);
                            }
                            if (localParent[vBRoot] == Constants.UNUSED) {
                                localParent[vBRoot] = vBRoot;
                                itemsList[vBRoot].set(vBRoot);
                            }
                            localParent[vARoot] = vBRoot;
                            itemsList[vBRoot].or(itemsList[vARoot]);
                        }
                    }
                }
            }
        }
        final TIntArrayList resultVertices = new TIntArrayList();
        final TIntArrayList resultEdges = new TIntArrayList();
        boolean connectedFlag = false;
        for (int j = 0; j < totalVertices; ++j) {
            if (localParent[j] == j && itemsList[j].get(source) && itemsList[j].get(destination)) {
                connectedFlag = true;
                break;
            }
        }
        if (!connectedFlag) {
            return new Region(resultVertices.toArray());
        }
        int j;
        int[] path;
        Edge uv;
        for (path = findShortestDistance(graph, adjacencyGraph, source, destination, this.shortestFlag, new BitSet(totalVertices), totalVertices), j = destination; path[j] != j; j = path[j]) {
            uv = graph.findEdge(graph.vertices().get(j), graph.vertices().get(path[j]));
            resultEdges.add(uv.index());
            resultVertices.add(j);
        }
        resultVertices.add(j);
        switch (this.resultType) {
            case Vertex -> {
                return new Region(resultVertices.toArray());
            }
            case Edge -> {
                return new Region(resultEdges.toArray());
            }
            default -> {
                return null;
            }
        }
    }
    
    private Region evalEdge(final Context context) {
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int numPlayers = context.game().players().count();
        final int[] localParent = new int[totalVertices];
        final BitSet[] itemsList = new BitSet[totalVertices];
        final BitSet[] adjacencyGraph = new BitSet[totalVertices];
        final int source = this.sourceFn.eval(context);
        final int destination = this.destinationFn.eval(context);
        for (int i = 0; i < totalVertices; ++i) {
            localParent[i] = Constants.UNUSED;
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
                if (vARoot != vBRoot) {
                    if (localParent[vARoot] == Constants.UNUSED) {
                        localParent[vARoot] = vARoot;
                        itemsList[vARoot].set(vARoot);
                    }
                    if (localParent[vBRoot] == Constants.UNUSED) {
                        localParent[vBRoot] = vBRoot;
                        itemsList[vBRoot].set(vBRoot);
                    }
                    localParent[vARoot] = vBRoot;
                    itemsList[vBRoot].or(itemsList[vARoot]);
                }
            }
        }
        final TIntArrayList resultVertices = new TIntArrayList();
        final TIntArrayList resultEdges = new TIntArrayList();
        boolean connectedFlag = false;
        for (int j = 0; j < totalVertices; ++j) {
            if (localParent[j] == j && itemsList[j].get(source) && itemsList[j].get(destination)) {
                connectedFlag = true;
                break;
            }
        }
        if (!connectedFlag) {
            return new Region(resultVertices.toArray());
        }
        int j;
        int[] path;
        Edge uv;
        for (path = findShortestDistance(graph, adjacencyGraph, source, destination, this.shortestFlag, new BitSet(totalVertices), totalVertices), j = destination; path[j] != j; j = path[j]) {
            uv = graph.findEdge(graph.vertices().get(j), graph.vertices().get(path[j]));
            resultEdges.add(uv.index());
            resultVertices.add(j);
        }
        resultVertices.add(j);
        switch (this.resultType) {
            case Vertex -> {
                return new Region(resultVertices.toArray());
            }
            case Edge -> {
                return new Region(resultEdges.toArray());
            }
            default -> {
                return null;
            }
        }
    }
    
    public static int[] findShortestDistance(final Topology graph, final BitSet[] adjacenceGraph, final int from, final int to, final boolean shortestFlag, final BitSet visited, final int totalVertices) {
        final Queue<Integer> toVisit = new PriorityQueue<>();
        final int[] dist = new int[totalVertices];
        final int[] path = new int[totalVertices];
        Arrays.fill(dist, 1000000000);
        toVisit.add(from);
        dist[from] = 0;
        path[from] = from;
        while (!toVisit.isEmpty()) {
            final int u = toVisit.remove();
            if (u == to) {
                return path;
            }
            if (visited.get(u)) {
                continue;
            }
            visited.set(u);
            for (int v = adjacenceGraph[u].nextSetBit(0); v >= 0; v = adjacenceGraph[u].nextSetBit(v + 1)) {
                final Edge uv = graph.findEdge(graph.vertices().get(v), graph.vertices().get(u));
                final int weight = uv.cost();
                if (shortestFlag) {
                    if (dist[v] > dist[u] + weight) {
                        dist[v] = dist[u] + weight;
                        path[v] = u;
                        toVisit.add(v);
                    }
                }
                else if (dist[v] >= dist[u] + weight) {
                    dist[v] = dist[u] + weight;
                    path[v] = u;
                    toVisit.add(v);
                }
            }
        }
        return path;
    }
    
    private int find(final int position, final int[] parent) {
        final int parentId = parent[position];
        if (parentId == position || parentId == Constants.UNUSED) {
            return position;
        }
        return this.find(parentId, parent);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= SiteType.stateFlags(this.resultType);
        gameFlags |= SiteType.stateFlags(this.ofType);
        gameFlags |= this.sourceFn.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.sourceFn.preprocess(game);
    }
}
