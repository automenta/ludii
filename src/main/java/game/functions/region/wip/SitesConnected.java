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
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import topology.Edge;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.BitSet;

@Hide
public final class SitesConnected extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final SiteType resultType;
    private final SiteType ofType;
    private final IntFunction startLocationFn;
    private final RegionFunction regionArray;
    private final boolean shortestFlag;
    private final boolean closedFlag;
    private final IntFunction who;
    
    public SitesConnected(final SiteType resultType, @Name final SiteType of, @Name final IntFunction at, final RegionFunction regions, @Opt @Name final Boolean shortest, @Opt @Name final Boolean closed, @Opt final Player who) {
        this.startLocationFn = at;
        this.regionArray = regions;
        this.ofType = of;
        this.resultType = resultType;
        this.shortestFlag = (shortest != null && shortest);
        this.closedFlag = (closed != null && closed);
        this.who = who.index();
    }
    
    @Override
    public Region eval(final Context context) {
        final int siteId = this.startLocationFn.eval(context);
        if (siteId == -1) {
            return null;
        }
        System.out.println("here");
        switch (this.ofType) {
            case Vertex -> {
                return null;
            }
            case Edge -> {
                return this.evalEdge(context, siteId);
            }
            case Cell -> {
                return null;
            }
            default -> {
                return null;
            }
        }
    }
    
    private Region evalEdge(final Context context, final int siteId) {
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int numPlayers = context.game().players().count();
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
                if (vARoot != vBRoot) {
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
        }
        final TIntArrayList resultOwner = new TIntArrayList();
        final BitSet regionList = new BitSet(totalVertices);
        boolean connectedFlag = false;
        for (int j = 0; j < totalVertices; ++j) {
            if (this.regionArray.contains(context, j)) {
                regionList.set(j);
            }
        }
        int j;
        for (j = 0; j < totalVertices; ++j) {
            if (localParent[j] == j && regionList.intersects(itemsList[j])) {
                final BitSet regionTemp = (BitSet)regionList.clone();
                regionTemp.and(itemsList[j]);
                regionTemp.xor(regionList);
                if (regionTemp.isEmpty()) {
                    connectedFlag = true;
                    break;
                }
            }
        }
        if (!connectedFlag) {
            return new Region(resultOwner.toArray());
        }
        switch (this.resultType) {
            case Vertex -> {
                for (int l = itemsList[j].nextSetBit(0); l >= 0; l = itemsList[j].nextSetBit(l + 1)) {
                    if (itemsList[j].get(l)) {
                        resultOwner.add(l);
                    }
                }
                return new Region(resultOwner.toArray());
            }
            case Edge -> {
                if (!this.shortestFlag && !this.closedFlag) {
                    for (int m = graph.edges().size() - 1; m >= 0; --m) {
                        if ((whoSiteId == numPlayers + 1 && state.who(m, SiteType.Edge) != 0) || (whoSiteId < numPlayers + 1 && state.who(m, SiteType.Edge) == whoSiteId)) {
                            final Edge kEdge2 = graph.edges().get(m);
                            final int vA2 = kEdge2.vA().index();
                            final int vB2 = kEdge2.vB().index();
                            if (itemsList[j].get(vA2) && itemsList[j].get(vB2)) {
                                resultOwner.add(m);
                            }
                        }
                    }
                    System.out.println("resultOwner :" + resultOwner);
                    return new Region(resultOwner.toArray());
                }
                final TIntArrayList allReqVertices = new TIntArrayList();
                for (int j2 = itemsList[j].nextSetBit(0); j2 >= 0; j2 = itemsList[j].nextSetBit(j2 + 1)) {
                    if (itemsList[j].get(j2)) {
                        allReqVertices.add(j2);
                    }
                }
                System.out.println("regionList.cardinate:" + regionList.cardinality());
                if (regionList.cardinality() == 2) {
                    final int source = regionList.nextSetBit(0);
                    final int destination = regionList.nextSetBit(1);
                    System.out.println("source:" + source);
                    System.out.println("destination:" + destination);
                }
                return new Region(resultOwner.toArray());
            }
            case Cell -> {
                return null;
            }
            default -> {
                return null;
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
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= SiteType.stateFlags(this.resultType);
        gameFlags |= SiteType.stateFlags(this.ofType);
        gameFlags |= this.startLocationFn.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.startLocationFn.preprocess(game);
    }
}
