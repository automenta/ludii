// 
// Decompiled by Procyon v0.5.36
// 

package util;

import annotations.Opt;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.state.containerState.ContainerState;

import java.util.Arrays;
import java.util.List;

public final class IsLoopAux
{
    private final AbsoluteDirection dirnChoice;
    
    public IsLoopAux(@Opt final AbsoluteDirection dirnChoice) {
        this.dirnChoice = ((dirnChoice == null) ? AbsoluteDirection.Adjacent : dirnChoice);
    }
    
    public boolean eval(final Context context, final int siteId) {
        SiteType type;
        if (context.game().isEdgeGame()) {
            type = SiteType.Edge;
        }
        else if (context.game().isCellGame()) {
            type = SiteType.Cell;
        }
        else {
            type = SiteType.Vertex;
        }
        if (siteId == -1) {
            return false;
        }
        final Topology topology = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = state.who(siteId, type);
        List<? extends TopologyElement> elements;
        if (type == SiteType.Vertex) {
            elements = context.game().board().topology().vertices();
        }
        else if (type == SiteType.Edge) {
            elements = context.game().board().topology().edges();
        }
        else if (type == SiteType.Cell) {
            elements = context.game().board().topology().cells();
        }
        else {
            elements = context.game().graphPlayElements();
        }
        if (this.dirnChoice == AbsoluteDirection.Adjacent) {
            final TIntArrayList adjacentItemsList = new TIntArrayList();
            final List<Step> steps = topology.trajectories().steps(type, siteId, this.dirnChoice);
            for (final Step step : steps) {
                if (step.from().siteType() == step.to().siteType()) {
                    adjacentItemsList.add(step.to().id());
                }
            }
            return loop(type, elements, siteId, state, adjacentItemsList, state.unionInfo(AbsoluteDirection.Adjacent)[whoSiteId]);
        }
        final TIntArrayList adjacentItemsList = new TIntArrayList();
        final List<Step> steps = topology.trajectories().steps(type, siteId, type, this.dirnChoice);
        for (final Step step : steps) {
            adjacentItemsList.add(step.to().id());
        }
        final TIntArrayList directionItemsList = new TIntArrayList();
        final List<Step> stepsDirectionsItemsList = topology.trajectories().steps(type, siteId, type, this.dirnChoice);
        for (final Step step2 : stepsDirectionsItemsList) {
            directionItemsList.add(step2.to().id());
        }
        int count = 0;
        for (int i = 0; i < directionItemsList.size(); ++i) {
            final int ni = directionItemsList.get(i);
            if (state.who(ni, type) == whoSiteId) {
                ++count;
            }
        }
        return count >= 2 && loopOthers(context, siteId, state, adjacentItemsList, directionItemsList, this.dirnChoice, state.unionInfo(AbsoluteDirection.Orthogonal)[whoSiteId]);
    }
    
    private static boolean loop(final SiteType type, final List<? extends TopologyElement> elements, final int siteId, final ContainerState state, final TIntArrayList nList, final UnionInfoD uf) {
        final int whoSiteId = state.who(siteId, type);
        final int numNeighbours = nList.size();
        final int[] localParent = new int[numNeighbours];
        int adjacentSetsNumber = 0;
        Arrays.fill(localParent, -1);
        for (int i = 0; i < numNeighbours; ++i) {
            final int ni = nList.getQuick(i);
            if (state.who(ni, type) == whoSiteId) {
                if (localParent[i] == -1) {
                    localParent[i] = i;
                }
                final TIntArrayList kList = elementsToIndices(elements.get(ni).adjacent());
                final TIntArrayList intersectionList = intersection(nList, kList);
                for (int j = 0; j < intersectionList.size(); ++j) {
                    final int nj = intersectionList.getQuick(j);
                    if (state.who(nj, type) == whoSiteId && ni != siteId) {
                        int m = 0;
                        while (m < numNeighbours) {
                            if (m != i && nj == nList.getQuick(m)) {
                                if (localParent[m] == -1) {
                                    localParent[m] = i;
                                    break;
                                }
                                int mRoot = m;
                                int iRoot = i;
                                while (mRoot != localParent[mRoot]) {
                                    mRoot = localParent[mRoot];
                                }
                                while (iRoot != localParent[iRoot]) {
                                    iRoot = localParent[iRoot];
                                }
                                localParent[iRoot] = localParent[mRoot];
                                break;
                            }
                            else {
                                ++m;
                            }
                        }
                    }
                }
            }
        }
        for (int k = 0; k < numNeighbours; ++k) {
            if (localParent[k] == k) {
                ++adjacentSetsNumber;
            }
        }
        if (adjacentSetsNumber > 1) {
            for (int i = 0; i < numNeighbours; ++i) {
                if (localParent[i] == i) {
                    final int rootI = find(nList.getQuick(i), uf);
                    for (int l = i + 1; l < numNeighbours; ++l) {
                        if (localParent[l] == l && uf.isSameGroup(rootI, nList.getQuick(l))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean loopOthers(final Context context, final int siteId, final ContainerState state, final TIntArrayList nList, final TIntArrayList directionItemsList, final AbsoluteDirection dirnChoice, final UnionInfoD uf) {
        SiteType type;
        if (context.game().isEdgeGame()) {
            type = SiteType.Edge;
        }
        else if (context.game().isCellGame()) {
            type = SiteType.Cell;
        }
        else {
            type = SiteType.Vertex;
        }
        final int whoSiteId = state.who(siteId, type);
        final int numNeighbours = nList.size();
        final int[] localParent = new int[numNeighbours];
        int adjacentSetsNumber = 0;
        Arrays.fill(localParent, -1);
        for (int i = 0; i < numNeighbours; ++i) {
            final int ni = nList.getQuick(i);
            if (state.who(ni, type) == whoSiteId) {
                boolean orthogonalPosition = false;
                for (int k = 0; k < directionItemsList.size(); ++k) {
                    final int oi = directionItemsList.getQuick(k);
                    if (ni == oi) {
                        orthogonalPosition = true;
                        break;
                    }
                }
                if (orthogonalPosition) {
                    if (localParent[i] == -1) {
                        localParent[i] = i;
                    }
                    final TIntArrayList kList = new TIntArrayList();
                    final Topology topology = context.topology();
                    final List<Step> steps = topology.trajectories().steps(type, ni, type, dirnChoice);
                    for (final Step step : steps) {
                        nList.add(step.to().id());
                    }
                    final TIntArrayList intersectionList = intersection(nList, kList);
                    for (int j = 0; j < intersectionList.size(); ++j) {
                        final int nj = intersectionList.getQuick(j);
                        if (state.who(nj, type) == whoSiteId && ni != siteId) {
                            int m = 0;
                            while (m < numNeighbours) {
                                if (m != i && nj == nList.getQuick(m)) {
                                    if (localParent[m] == -1) {
                                        localParent[m] = i;
                                        break;
                                    }
                                    int mRoot = m;
                                    int iRoot = i;
                                    while (mRoot != localParent[mRoot]) {
                                        mRoot = localParent[mRoot];
                                    }
                                    while (iRoot != localParent[iRoot]) {
                                        iRoot = localParent[iRoot];
                                    }
                                    localParent[iRoot] = localParent[mRoot];
                                    break;
                                }
                                else {
                                    ++m;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int l = 0; l < numNeighbours; ++l) {
            if (localParent[l] == l) {
                ++adjacentSetsNumber;
            }
        }
        if (adjacentSetsNumber > 1) {
            for (int i = 0; i < numNeighbours; ++i) {
                if (localParent[i] == i) {
                    final int rootI = find(nList.getQuick(i), uf);
                    for (int j2 = i + 1; j2 < numNeighbours; ++j2) {
                        if (localParent[j2] == j2 && uf.isSameGroup(rootI, nList.getQuick(j2))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private static int find(final int position, final UnionInfoD uf) {
        final int parentId = uf.getParent(position);
        if (parentId == position) {
            return position;
        }
        return find(uf.getParent(parentId), uf);
    }
    
    public static TIntArrayList elementsToIndices(final List<? extends TopologyElement> elementsList) {
        final int verticesListSz = elementsList.size();
        final TIntArrayList indicesList = new TIntArrayList(verticesListSz);
        for (TopologyElement topologyElement : elementsList) {
            indicesList.add(topologyElement.index());
        }
        return indicesList;
    }
    
    public static boolean validDirection(final TIntArrayList verticesList, final int cell) {
        for (int verticesListSz = verticesList.size(), i = 0; i < verticesListSz; ++i) {
            if (verticesList.getQuick(i) == cell) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean adjacentCells(final TIntArrayList verticesList, final int cell) {
        for (int verticesListSz = verticesList.size(), i = 0; i < verticesListSz; ++i) {
            if (verticesList.getQuick(i) == cell) {
                return true;
            }
        }
        return false;
    }
    
    public static TIntArrayList intersection(final TIntArrayList list1, final TIntArrayList list2) {
        final TIntArrayList list3 = new TIntArrayList();
        for (int i = 0; i < list1.size(); ++i) {
            if (list2.contains(list1.getQuick(i))) {
                list3.add(list1.getQuick(i));
            }
        }
        return list3;
    }
    
    @Override
    public String toString() {
        String str = "";
        str += "IsLoopAux( )";
        return str;
    }
}
