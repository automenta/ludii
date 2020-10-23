// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.path;

import annotations.*;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.last.LastTo;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Edge;
import topology.Topology;
import topology.Vertex;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.*;

@Hide
public class IsPath extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final SiteType indexType;
    private final IntFunction who;
    private final IntFunction componentSize;
    private final IntFunction maxComponentSize;
    private final boolean closedFlag;
    
    public IsPath(final SiteType type, @Or final Player who, @Or final RoleType role, @Opt @Or2 @Name final IntFunction length, @Opt @Or2 @Name final IntFunction maxLimit, @Opt @Name final Boolean closed) {
        this.indexType = type;
        this.who = ((who == null) ? new Id(null, role) : who.index());
        this.componentSize = ((length == null) ? new IntConstant(0) : length);
        this.maxComponentSize = ((maxLimit == null) ? new IntConstant(0) : maxLimit);
        this.closedFlag = (closed != null && closed);
    }
    
    @Override
    public boolean eval(final Context context) {
        final int siteId = new LastTo(null).eval(context);
        if (siteId == -1) {
            return false;
        }
        switch (this.indexType) {
            case Vertex: {
                return this.evalVertex(context, siteId);
            }
            case Edge: {
                return this.evalEdge(context, siteId);
            }
            case Cell: {
                return this.evalCell(context, siteId);
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean evalEdge(final Context context, final int siteId) {
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int minComponentsize = this.componentSize.eval(context);
        final int maxRangeComponentsize = this.maxComponentSize.eval(context);
        final int[] disc = new int[totalVertices];
        final int[] low = new int[totalVertices];
        final BitSet stackMember = new BitSet(totalVertices);
        final Stack<Integer> st = new Stack<>();
        final Edge kEdge = graph.edges().get(siteId);
        final int v1 = kEdge.vA().index();
        final int v2 = kEdge.vB().index();
        final int startingVertex = v1;
        final BitSet testBitset = new BitSet(totalVertices);
        final BitSet edgeBitset = new BitSet(totalEdges);
        final BitSet[] adjacencyGraph = new BitSet[totalVertices];
        for (int i = 0; i < totalVertices; ++i) {
            adjacencyGraph[i] = new BitSet(totalEdges);
        }
        final int strongComponents = this.strongComponent(context, startingVertex, -1, disc, low, st, stackMember, testBitset, whoSiteId, 1, totalVertices, v1, v2);
        if (this.closedFlag) {
            for (int j = 0; j < totalEdges; ++j) {
                if (state.who(j, this.indexType) == whoSiteId) {
                    final Edge iEdge = graph.edges().get(j);
                    final int vA = iEdge.vA().index();
                    final int vB = iEdge.vB().index();
                    adjacencyGraph[vA].set(vB);
                    adjacencyGraph[vB].set(vA);
                    if (testBitset.get(vA) && testBitset.get(vB)) {
                        edgeBitset.set(j);
                    }
                }
            }
            if (minComponentsize != 0 && strongComponents == edgeBitset.cardinality() && strongComponents == minComponentsize) {
                return true;
            }
            if (maxRangeComponentsize > 2 && strongComponents <= maxRangeComponentsize && strongComponents == edgeBitset.cardinality() && strongComponents > 2) {
                return true;
            }
            if (edgeBitset.cardinality() > strongComponents) {
                final int[] path = findShortestDistance(graph, adjacencyGraph, v1, v2, new BitSet(totalVertices), totalVertices);
                int k = v2;
                int minDepth = 1;
                while (path[k] != k) {
                    ++minDepth;
                    k = path[k];
                }
                if (minComponentsize != 0 && minDepth == minComponentsize) {
                    return true;
                }
                return maxRangeComponentsize > 2 && minDepth <= maxRangeComponentsize;
            }
        }
        else {
            if (strongComponents != 0) {
                return false;
            }
            for (int j = 0; j < totalEdges; ++j) {
                final Edge iEdge = graph.edges().get(j);
                if (state.who(iEdge.index(), this.indexType) == whoSiteId) {
                    edgeBitset.set(j);
                }
            }
            final BitSet depthBitset1 = new BitSet(totalVertices);
            final BitSet depthBitset2 = new BitSet(totalVertices);
            final BitSet visitedEdge = new BitSet(totalEdges);
            int componentSz;
            if (minComponentsize != 0) {
                componentSz = minComponentsize;
            }
            else {
                componentSz = maxRangeComponentsize;
            }
            this.dfsMinPathEdge(context, graph, kEdge, edgeBitset, visitedEdge, 0, v1, v2, componentSz, depthBitset1, whoSiteId);
            this.dfsMinPathEdge(context, graph, kEdge, edgeBitset, visitedEdge, 0, v2, v1, componentSz, depthBitset2, whoSiteId);
            final int pathLength = depthBitset1.cardinality() - 1 + (depthBitset2.cardinality() - 1) + 1;
            if (minComponentsize != 0 && pathLength == minComponentsize && visitedEdge.cardinality() + 1 == pathLength) {
                return true;
            }
            return maxRangeComponentsize != 0 && pathLength <= maxRangeComponentsize && visitedEdge.cardinality() + 1 == pathLength;
        }
        return false;
    }
    
    private boolean evalCell(final Context context, final int siteId) {
        final Topology graph = context.topology();
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        final int whoSiteId = this.who.eval(context);
        final int totalCells = graph.cells().size();
        final int minComponentsize = this.componentSize.eval(context);
        final int maxRangeComponentsize = this.maxComponentSize.eval(context);
        final int[] disc = new int[totalCells];
        final int[] low = new int[totalCells];
        final BitSet stackMember = new BitSet(totalCells);
        final Stack<Integer> st = new Stack<>();
        final Cell kCell = graph.cells().get(siteId);
        boolean isolated = true;
        final List<Cell> nList = kCell.adjacent();
        final int v1 = kCell.index();
        int v2 = 0;
        final int startingVertex = v1;
        for (int i = 0; i < nList.size(); ++i) {
            final Cell iVertex = nList.get(i);
            if (iVertex != kCell && state.who(iVertex.index(), this.indexType) == whoSiteId) {
                v2 = iVertex.index();
                isolated = false;
                break;
            }
        }
        if (isolated && this.closedFlag) {
            return false;
        }
        if (isolated && !this.closedFlag && (minComponentsize == 1 || maxRangeComponentsize == 1)) {
            return true;
        }
        final BitSet testBitset = new BitSet(totalCells);
        final int[] vertexIndex = new int[totalCells];
        final int[] vertexVisit = new int[totalCells];
        final int strongComponents = this.strongComponent(context, startingVertex, -1, disc, low, st, stackMember, testBitset, whoSiteId, 1, totalCells, v1, v2);
        if (this.closedFlag) {
            if (minComponentsize != 0) {
                if (strongComponents == minComponentsize) {
                    return true;
                }
                if (minComponentsize < strongComponents) {
                    final TIntArrayList nList2 = new TIntArrayList();
                    final List<Cell> nList3 = graph.cells().get(kCell.index()).adjacent();
                    for (int j = 0; j < nList3.size(); ++j) {
                        final Cell iVertex2 = nList3.get(j);
                        if (state.who(iVertex2.index(), this.indexType) == whoSiteId) {
                            nList2.add(iVertex2.index());
                        }
                    }
                    for (int j = 0; j < nList2.size(); ++j) {
                        final int minDepth = this.dfsMinCycleSzVertexCell(context, graph, vertexVisit, vertexIndex, 1, kCell.index(), nList2.get(j), -1, 1000000000, whoSiteId);
                        if (minDepth == minComponentsize && minDepth > 2) {
                            return true;
                        }
                    }
                }
            }
            else if (maxRangeComponentsize != 0) {
                if (strongComponents <= maxRangeComponentsize && strongComponents > 2) {
                    return true;
                }
                if (strongComponents > maxRangeComponentsize) {
                    final TIntArrayList nList2 = new TIntArrayList();
                    final List<Cell> nList3 = graph.cells().get(kCell.index()).adjacent();
                    for (int j = 0; j < nList3.size(); ++j) {
                        final Cell iVertex2 = nList3.get(j);
                        if (state.who(iVertex2.index(), this.indexType) == whoSiteId) {
                            nList2.add(iVertex2.index());
                        }
                    }
                    for (int j = 0; j < nList2.size(); ++j) {
                        final int minDepth = this.dfsMinCycleSzVertexCell(context, graph, vertexVisit, vertexIndex, 1, kCell.index(), nList2.get(j), -1, 1000000000, whoSiteId);
                        if (minDepth <= maxRangeComponentsize && minDepth > 2) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        if (strongComponents != 0) {
            return false;
        }
        if (maxRangeComponentsize > 0) {
            return true;
        }
        final List<Cell> nListVertex = graph.cells().get(kCell.index()).adjacent();
        final TIntArrayList nList4 = new TIntArrayList();
        for (int j = 0; j < nListVertex.size(); ++j) {
            if (state.whoCell(nListVertex.get(j).index()) == whoSiteId) {
                nList4.add(nListVertex.get(j).index());
            }
        }
        if (nList4.size() > 2 || nList4.size() < 1) {
            return false;
        }
        int pathSize1 = 0;
        if (nList4.size() == 1) {
            pathSize1 = this.dfsMinPathSzVertexCell(context, 0, kCell.index(), v1, -1, minComponentsize, whoSiteId) + 1;
        }
        if (pathSize1 == minComponentsize) {
            return true;
        }
        int pathSize2 = 0;
        if (nList4.size() == 2) {
            pathSize1 = this.dfsMinPathSzVertexCell(context, 1, kCell.index(), nList4.getQuick(0), -1, minComponentsize, whoSiteId);
            pathSize2 = this.dfsMinPathSzVertexCell(context, 1, kCell.index(), nList4.getQuick(1), nList4.getQuick(0), minComponentsize, whoSiteId);
            final int pathSize3 = pathSize1 + pathSize2 + 1;
            return pathSize3 == minComponentsize;
        }
        return false;
    }
    
    private boolean evalVertex(final Context context, final int siteId) {
        final Topology graph = context.topology();
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        final int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final int minComponentsize = this.componentSize.eval(context);
        final int maxRangeComponentsize = this.maxComponentSize.eval(context);
        final int[] disc = new int[totalVertices];
        final int[] low = new int[totalVertices];
        final BitSet stackMember = new BitSet(totalVertices);
        final Stack<Integer> st = new Stack<>();
        final Vertex kVertex = graph.vertices().get(siteId);
        boolean isolated = true;
        final List<Vertex> nList = kVertex.adjacent();
        final int v1 = kVertex.index();
        int v2 = 0;
        final int startingVertex = v1;
        for (int i = 0; i < nList.size(); ++i) {
            final Vertex iVertex = nList.get(i);
            if (iVertex != kVertex && state.who(iVertex.index(), this.indexType) == whoSiteId) {
                v2 = iVertex.index();
                isolated = false;
                break;
            }
        }
        if (isolated && this.closedFlag) {
            return false;
        }
        if (isolated && !this.closedFlag && (minComponentsize == 1 || maxRangeComponentsize == 1)) {
            return true;
        }
        final BitSet testBitset = new BitSet(totalVertices);
        final BitSet edgeBitset = new BitSet(totalEdges);
        final int[] vertexIndex = new int[totalVertices];
        final int[] vertexVisit = new int[totalVertices];
        final int strongComponents = this.strongComponent(context, startingVertex, -1, disc, low, st, stackMember, testBitset, whoSiteId, 1, totalVertices, v1, v2);
        if (this.closedFlag) {
            if (minComponentsize != 0) {
                if (strongComponents == minComponentsize) {
                    return true;
                }
                if (strongComponents > minComponentsize) {
                    final TIntArrayList nListVertex = this.vertexToAdjacentNeighbourVertices1(context, kVertex.index(), whoSiteId);
                    for (int j = 0; j < nListVertex.size(); ++j) {
                        final int minDepth = this.dfsMinCycleSzVertexCell(context, graph, vertexVisit, vertexIndex, 1, kVertex.index(), nListVertex.get(j), -1, 1000000000, whoSiteId);
                        if (minDepth == minComponentsize && minDepth > 2) {
                            return true;
                        }
                    }
                }
            }
            else if (maxRangeComponentsize != 0) {
                if (strongComponents <= maxRangeComponentsize && strongComponents > 2) {
                    return true;
                }
                if (strongComponents > maxRangeComponentsize) {
                    final TIntArrayList nListVertex = this.vertexToAdjacentNeighbourVertices1(context, kVertex.index(), whoSiteId);
                    for (int j = 0; j < nListVertex.size(); ++j) {
                        final int minDepth = this.dfsMinCycleSzVertexCell(context, graph, vertexVisit, vertexIndex, 1, kVertex.index(), nListVertex.get(j), -1, 1000000000, whoSiteId);
                        if (minDepth <= maxRangeComponentsize && minDepth > 2) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        if (strongComponents != 0) {
            return false;
        }
        if (maxRangeComponentsize > 0) {
            return true;
        }
        final TIntArrayList nListVertex = this.vertexToAdjacentNeighbourVertices1(context, kVertex.index(), whoSiteId);
        if (nListVertex.size() > 2 || nListVertex.size() < 1) {
            return false;
        }
        for (int j = 0; j < totalEdges; ++j) {
            final Edge iEdge = graph.edges().get(j);
            if (state.who(iEdge.index(), this.indexType) == whoSiteId) {
                edgeBitset.set(j);
            }
        }
        if (minComponentsize == 0) {
            return false;
        }
        int pathSize = 0;
        if (nListVertex.size() == 1) {
            pathSize = this.dfsMinPathSzVertexCell(context, 0, kVertex.index(), v1, -1, minComponentsize, whoSiteId) + 1;
        }
        if (pathSize == minComponentsize) {
            return true;
        }
        int pathSize2 = 0;
        int pathSize3 = 0;
        if (nListVertex.size() == 2) {
            pathSize2 = this.dfsMinPathSzVertexCell(context, 1, kVertex.index(), nListVertex.getQuick(0), -1, minComponentsize, whoSiteId);
            pathSize3 = this.dfsMinPathSzVertexCell(context, 1, kVertex.index(), nListVertex.getQuick(1), nListVertex.getQuick(0), minComponentsize, whoSiteId);
            final int path = pathSize2 + pathSize3 + 1;
            return path == minComponentsize;
        }
        return false;
    }
    
    private int strongComponent(final Context context, final int presentPosition, final int parent, final int[] visit, final int[] low, final Stack<Integer> stackInfo, final BitSet stackInfoBitset, final BitSet testBitset1, final int whoSiteId, final int index, final int totalItems, final int v1, final int v2) {
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        low[presentPosition] = (visit[presentPosition] = index);
        stackInfo.push(presentPosition);
        stackInfoBitset.set(presentPosition);
        TIntArrayList nList = new TIntArrayList();
        if (this.indexType.equals(SiteType.Cell)) {
            final List<Cell> nList2 = graph.cells().get(presentPosition).adjacent();
            for (int i = 0; i < nList2.size(); ++i) {
                final Cell iVertex = nList2.get(i);
                if (state.who(iVertex.index(), this.indexType) == whoSiteId) {
                    nList.add(iVertex.index());
                }
            }
        }
        else if (this.indexType.equals(SiteType.Vertex)) {
            nList = this.vertexToAdjacentNeighbourVertices1(context, presentPosition, whoSiteId);
        }
        else if (this.indexType.equals(SiteType.Edge)) {
            nList = this.vertexToAdjacentNeighbourVertices(context, presentPosition, whoSiteId);
        }
        for (int j = 0; j != nList.size(); ++j) {
            final int v3 = nList.get(j);
            if (v3 != parent) {
                if (visit[v3] == 0) {
                    this.strongComponent(context, v3, presentPosition, visit, low, stackInfo, stackInfoBitset, testBitset1, whoSiteId, index + 1, totalItems, v1, v2);
                    low[presentPosition] = ((low[presentPosition] < low[v3]) ? low[presentPosition] : low[v3]);
                }
                else if (stackInfoBitset.get(v3)) {
                    low[presentPosition] = ((low[presentPosition] < visit[v3]) ? low[presentPosition] : visit[v3]);
                }
            }
        }
        int w = 0;
        final BitSet testBitset2 = new BitSet(totalItems);
        if (low[presentPosition] == visit[presentPosition]) {
            while (stackInfo.peek() != presentPosition) {
                w = stackInfo.peek();
                stackInfoBitset.clear(w);
                testBitset2.set(w);
                stackInfo.pop();
            }
            w = stackInfo.peek();
            stackInfoBitset.clear(w);
            testBitset2.set(w);
            stackInfo.pop();
        }
        if (testBitset2.get(v1) && testBitset2.get(v2)) {
            for (int k = testBitset2.nextSetBit(0); k >= 0; k = testBitset2.nextSetBit(k + 1)) {
                testBitset1.set(k);
            }
            return testBitset2.cardinality();
        }
        return 0;
    }
    
    public static int[] findShortestDistance(final Topology graph, final BitSet[] adjacenceGraph, final int from, final int to, final BitSet visited, final int totalVertices) {
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
            final Edge kEdge = graph.findEdge(graph.vertices().get(to), graph.vertices().get(from));
            for (int v = adjacenceGraph[u].nextSetBit(0); v >= 0; v = adjacenceGraph[u].nextSetBit(v + 1)) {
                final Edge uv = graph.findEdge(graph.vertices().get(v), graph.vertices().get(u));
                if (uv != kEdge) {
                    final int weight = 1;
                    if (dist[v] > dist[u] + 1) {
                        dist[v] = dist[u] + 1;
                        path[v] = u;
                        toVisit.add(v);
                    }
                }
            }
        }
        return path;
    }
    
    private int dfsMinCycleSzVertexCell(final Context context, final Topology graph, final int[] vertexVisit, final int[] vertexIndex, final int index, final int startingVertex, final int presentVertex, final int parent, final int minDepth, final int whoSiteId) {
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        final int presentDegree = graph.vertices().get(presentVertex).adjacent().size();
        int newindex = 0;
        int newMinDepth = 0;
        ++vertexVisit[presentVertex];
        if (vertexVisit[presentVertex] > presentDegree) {
            return index;
        }
        if (minDepth == 3) {
            return minDepth;
        }
        if (vertexIndex[presentVertex] == 0) {
            vertexIndex[presentVertex] = index;
            newindex = index;
        }
        else {
            newindex = vertexIndex[presentVertex];
        }
        newMinDepth = minDepth;
        if (startingVertex == presentVertex && minDepth > index) {
            newMinDepth = index;
            return newMinDepth + 1;
        }
        if (this.indexType.equals(SiteType.Cell)) {
            final List<Cell> nList1 = graph.cells().get(presentVertex).adjacent();
            for (int i = 0; i < nList1.size(); ++i) {
                final int iVertex = nList1.get(i).index();
                if (iVertex != parent && state.who(iVertex, this.indexType) == whoSiteId) {
                    this.dfsMinCycleSzVertexCell(context, graph, vertexVisit, vertexIndex, newindex + 1, startingVertex, iVertex, presentVertex, newMinDepth, whoSiteId);
                }
            }
        }
        else if (this.indexType.equals(SiteType.Vertex)) {
            final TIntArrayList nListVertex = this.vertexToAdjacentNeighbourVertices1(context, presentVertex, whoSiteId);
            for (int i = 0; i < nListVertex.size(); ++i) {
                final int ni = nListVertex.get(i);
                if (newindex == 1 && ni != startingVertex && presentVertex != ni) {
                    this.dfsMinCycleSzVertexCell(context, graph, vertexVisit, vertexIndex, newindex + 1, startingVertex, ni, presentVertex, newMinDepth, whoSiteId);
                }
            }
        }
        return newindex + 1;
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
    
    private int dfsMinPathSzVertexCell(final Context context, final int index, final int startingVertex, final int presentVertex, final int parent, final int mincomponentsz, final int whoSiteId) {
        final Topology graph = context.topology();
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        if (index == mincomponentsz * 2) {
            return index;
        }
        TIntArrayList nListVertex = new TIntArrayList();
        if (this.indexType.equals(SiteType.Cell)) {
            final List<Cell> nList = graph.cells().get(presentVertex).adjacent();
            for (int i = 0; i < nList.size(); ++i) {
                if (state.whoCell(nList.get(i).index()) == whoSiteId) {
                    nListVertex.add(nList.get(i).index());
                }
            }
        }
        if (this.indexType.equals(SiteType.Vertex)) {
            nListVertex = this.vertexToAdjacentNeighbourVertices1(context, presentVertex, whoSiteId);
        }
        if (nListVertex.size() > 2) {
            return 1000000000;
        }
        if (nListVertex.size() == 0) {
            return index;
        }
        for (int j = 0; j < nListVertex.size(); ++j) {
            if (nListVertex.getQuick(j) != parent && nListVertex.getQuick(j) != startingVertex) {
                return this.dfsMinPathSzVertexCell(context, index + 1, startingVertex, nListVertex.get(j), presentVertex, mincomponentsz, whoSiteId);
            }
        }
        return index;
    }
    
    private TIntArrayList vertexToAdjacentNeighbourVertices(final Context context, final int v, final int whoSiteId) {
        final ContainerState state = context.state().containerStates()[0];
        final int totalEdges = context.topology().edges().size();
        final TIntArrayList nList = new TIntArrayList();
        for (int k = 0; k < totalEdges; ++k) {
            final Edge kEdge = context.topology().edges().get(k);
            if (state.who(kEdge.index(), this.indexType) == whoSiteId) {
                final int vA = kEdge.vA().index();
                final int vB = kEdge.vB().index();
                if (vA == v) {
                    nList.add(vB);
                }
                else if (vB == v) {
                    nList.add(vA);
                }
            }
        }
        return nList;
    }
    
    private TIntArrayList vertexToAdjacentNeighbourVertices1(final Context context, final int v, final int whoSiteId) {
        final ContainerState state = context.state().containerStates()[0];
        final TIntArrayList nList = new TIntArrayList();
        final List<Vertex> nList2 = context.topology().vertices().get(v).adjacent();
        for (int k = 0; k < nList2.size(); ++k) {
            if (nList2.get(k).index() != v && state.who(nList2.get(k).index(), this.indexType) == whoSiteId) {
                nList.add(nList2.get(k).index());
            }
        }
        return nList;
    }
    
    @Override
    public String toString() {
        String str = "";
        str += "IsPath( )";
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = 8388608L;
        return 8388608L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
