// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.AbsoluteDirection;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import main.Constants;
import topology.Edge;
import topology.Topology;
import topology.TopologyElement;
import util.state.containerState.ContainerState;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;

public class UnionFindD implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private UnionFindD() {
    }
    
    public static boolean eval(final Context context, final boolean activeIsLoop, final int siteId, final AbsoluteDirection dirnChoice) {
        final SiteType type = getSiteType(context);
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = state.who(siteId, type);
        final int numPlayers = context.game().players().count();
        final int whoSiteIdNext = context.state().next();
        final TIntArrayList nList = new TIntArrayList();
        final List<? extends TopologyElement> elements = context.game().graphPlayElements();
        final Topology topology = context.topology();
        if (whoSiteId == 0) {
            if (state.what(siteId, type) != 0) {
                evalUFGT(context, siteId);
            }
            return false;
        }
        if (whoSiteId < 1 || whoSiteId > numPlayers) {
            return false;
        }
        if (siteId < 0 || siteId >= elements.size()) {
            return false;
        }
        boolean ringFlag = false;
        if (state.unionInfo(dirnChoice)[whoSiteIdNext].getParent(siteId) != Constants.UNUSED) {
            evalD(context, siteId, true, dirnChoice);
        }
        final List<Step> steps = topology.trajectories().steps(type, siteId, type, dirnChoice);
        for (final Step step : steps) {
            nList.add(step.to().id());
        }
        if (activeIsLoop) {
            ringFlag = new IsLoopAux(dirnChoice).eval(context, siteId);
        }
        context.setRingFlagCalled(ringFlag);
        final TIntArrayList neighbourList = preProcessingLiberties(type, state, siteId, numPlayers, nList, state.unionInfo(dirnChoice)[whoSiteId], whoSiteId);
        union(siteId, neighbourList, true, state.unionInfo(dirnChoice)[whoSiteId], whoSiteId);
        final TIntArrayList neighbourListCommon = preProcessingLiberties(type, state, siteId, numPlayers, nList, state.unionInfo(dirnChoice)[numPlayers + 1], numPlayers + 1);
        union(siteId, neighbourListCommon, true, state.unionInfo(dirnChoice)[numPlayers + 1], numPlayers + 1);
        return ringFlag;
    }
    
    public static void determineUnionTree(final Context context, final int deleteId, final AbsoluteDirection dirnChoice) {
        final SiteType type = getSiteType(context);
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        final int deletePlayer = state.who(deleteId, type);
        if (deletePlayer == 0) {
            if (context.game().isGraphGame()) {
                evalDeleteGT(context, deleteId);
            }
        }
        else if (state.unionInfo(dirnChoice)[deletePlayer].getParent(deleteId) == Constants.UNUSED) {
            evalD(context, deleteId, true, dirnChoice);
        }
        else {
            evalNoLibertyFriendlyD(context, deleteId, dirnChoice);
        }
    }
    
    public static void evalD(final Context context, final int deleteId, final boolean enemy, final AbsoluteDirection dirnChoice) {
        final SiteType type = getSiteType(context);
        final ContainerState state = context.state().containerStates()[0];
        int deletePlayer = state.who(deleteId, type);
        final int numPlayers = context.game().players().count();
        if (context.game().isGraphGame() && deletePlayer == 0) {
            evalDeleteGT(context, deleteId);
            return;
        }
        if (enemy) {
            deletePlayer = context.state().next();
        }
        deletion(type, context, deleteId, dirnChoice, true, false, state.unionInfo(dirnChoice)[deletePlayer], deletePlayer);
        deletion(type, context, deleteId, dirnChoice, true, false, state.unionInfo(dirnChoice)[numPlayers + 1], numPlayers + 1);
    }
    
    private static void deletion(final SiteType type, final Context context, final int deleteLoc, final AbsoluteDirection dirnChoice, final boolean libertyFlag, final boolean blockingFlag, final UnionInfoD uf, final int whoSiteId) {
        final int cid = context.containerId()[deleteLoc];
        final ContainerState state = context.state().containerStates()[cid];
        final int numPlayers = context.game().players().count();
        final int root = find(deleteLoc, uf);
        final BitSet bitsetsDeletePlayer = (BitSet)uf.getItemsList(root).clone();
        final Topology topology = context.topology();
        for (int i = bitsetsDeletePlayer.nextSetBit(0); i >= 0; i = bitsetsDeletePlayer.nextSetBit(i + 1)) {
            uf.clearParent(i);
            uf.clearItemsList(i);
            if (libertyFlag) {
                uf.clearAllitemWithOrthoNeighbors(i);
            }
        }
        bitsetsDeletePlayer.clear(deleteLoc);
        for (int i = bitsetsDeletePlayer.nextSetBit(0); i >= 0; i = bitsetsDeletePlayer.nextSetBit(i + 1)) {
            final List<Step> steps = topology.trajectories().steps(type, i, type, dirnChoice);
            final TIntArrayList nList = new TIntArrayList(steps.size());
            for (final Step step : steps) {
                nList.add(step.to().id());
            }
            if (!blockingFlag) {
                final int nListSz = nList.size();
                final TIntArrayList neighbourList = new TIntArrayList(nListSz);
                for (int j = 0; j < nListSz; ++j) {
                    final int ni = nList.getQuick(j);
                    final int who = state.who(ni, type);
                    if ((who == whoSiteId && whoSiteId != numPlayers + 1) || (who != whoSiteId && whoSiteId == numPlayers + 1)) {
                        neighbourList.add(ni);
                    }
                }
                union(i, neighbourList, false, uf, whoSiteId);
            }
            else {
                union(i, nList, libertyFlag, uf, whoSiteId);
            }
        }
    }
    
    public static void evalSetGT(final Context context, final int siteId, final RoleType role, final AbsoluteDirection dir) {
        final SiteType type = getSiteType(context);
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = state.who(siteId, type);
        if (whoSiteId == 0) {
            unionSetGT(context, siteId, dir);
        }
    }
    
    private static void unionSetGT(final Context context, final int siteId, final AbsoluteDirection dir) {
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int numplayers = context.game().players().count();
        final Edge kEdge = graph.edges().get(siteId);
        final int vA = kEdge.vA().index();
        final int vB = kEdge.vB().index();
        for (int i = 1; i <= numplayers + 1; ++i) {
            final int player = i;
            final int rootP = find(vA, state.unionInfo(dir)[player]);
            final int rootQ = find(vB, state.unionInfo(dir)[player]);
            if (rootP == rootQ) {
                return;
            }
            if (state.unionInfo(dir)[player].getGroupSize(rootP) == 0) {
                state.unionInfo(dir)[player].setParent(vA, vA);
                state.unionInfo(dir)[player].setItem(vA, vA);
            }
            if (state.unionInfo(dir)[player].getGroupSize(rootQ) == 0) {
                state.unionInfo(dir)[player].setParent(vB, vB);
                state.unionInfo(dir)[player].setItem(vB, vB);
            }
            if (state.unionInfo(dir)[player].getGroupSize(rootP) < state.unionInfo(dir)[player].getGroupSize(rootQ)) {
                state.unionInfo(dir)[player].setParent(rootP, rootQ);
                state.unionInfo(dir)[player].mergeItemsLists(rootQ, rootP);
            }
            else {
                state.unionInfo(dir)[player].setParent(rootQ, rootP);
                state.unionInfo(dir)[player].mergeItemsLists(rootP, rootQ);
            }
        }
    }
    
    private static void evalUFGT(final Context context, final int siteId) {
        final ContainerState state = context.state().containerStates()[0];
        final int numplayers = context.game().players().count();
        if (!context.game().isGraphGame()) {
            return;
        }
        final SiteType type = SiteType.Edge;
        final int player = state.who(siteId, type);
        if (player < 1 || player > numplayers + 1) {
            return;
        }
        if (player >= 1 && player <= numplayers) {
            unionGT(context, siteId, player);
            unionGT(context, siteId, numplayers + 1);
        }
        else {
            for (int i = 1; i <= numplayers + 1; ++i) {
                unionGT(context, siteId, i);
            }
        }
    }
    
    private static void unionGT(final Context context, final int siteId, final int player) {
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final Edge kEdge = graph.edges().get(siteId);
        final int vA = kEdge.vA().index();
        final int vB = kEdge.vB().index();
        final int rootP = find(vA, state.unionInfo(AbsoluteDirection.Adjacent)[player]);
        final int rootQ = find(vB, state.unionInfo(AbsoluteDirection.Adjacent)[player]);
        if (rootP == rootQ) {
            return;
        }
        if (state.unionInfo(AbsoluteDirection.Adjacent)[player].getGroupSize(rootP) == 0) {
            state.unionInfo(AbsoluteDirection.Adjacent)[player].setParent(vA, vA);
            state.unionInfo(AbsoluteDirection.Adjacent)[player].setItem(vA, vA);
        }
        if (state.unionInfo(AbsoluteDirection.Adjacent)[player].getGroupSize(rootQ) == 0) {
            state.unionInfo(AbsoluteDirection.Adjacent)[player].setParent(vB, vB);
            state.unionInfo(AbsoluteDirection.Adjacent)[player].setItem(vB, vB);
        }
        if (state.unionInfo(AbsoluteDirection.Adjacent)[player].getGroupSize(rootP) < state.unionInfo(AbsoluteDirection.Adjacent)[player].getGroupSize(rootQ)) {
            state.unionInfo(AbsoluteDirection.Adjacent)[player].setParent(rootP, rootQ);
            state.unionInfo(AbsoluteDirection.Adjacent)[player].mergeItemsLists(rootQ, rootP);
        }
        else {
            state.unionInfo(AbsoluteDirection.Adjacent)[player].setParent(rootQ, rootP);
            state.unionInfo(AbsoluteDirection.Adjacent)[player].mergeItemsLists(rootP, rootQ);
        }
    }
    
    private static void evalDeleteGT(final Context context, final int deleteId) {
        final ContainerState state = context.state().containerStates()[0];
        final SiteType type = SiteType.Edge;
        final int player = state.who(deleteId, type);
        final int numplayers = context.game().players().count();
        if (player < 1 || player > numplayers + 1) {
            return;
        }
        if (player >= 1 && player <= numplayers) {
            deleteGT(context, deleteId, player, player);
            deleteGT(context, deleteId, numplayers + 1, player);
        }
        else {
            for (int i = 1; i <= numplayers + 1; ++i) {
                deleteGT(context, deleteId, i, player);
            }
        }
    }
    
    private static void deleteGT(final Context context, final int deleteId, final int playerUF, final int compareId) {
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int totalEdges = graph.edges().size();
        final int totalVertices = graph.vertices().size();
        final int numplayers = context.game().players().count();
        for (int i = 0; i < totalVertices; ++i) {
            state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].clearParent(i);
            state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].clearItemsList(i);
            state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].setParent(i, i);
            state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].setItem(i, i);
        }
        for (int k = 0; k < totalEdges; ++k) {
            if (k != deleteId && ((playerUF <= numplayers && (state.who(k, SiteType.Edge) == compareId || state.who(k, SiteType.Edge) == numplayers + 1)) || (playerUF == numplayers + 1 && state.who(k, SiteType.Edge) != 0))) {
                final Edge kEdge = graph.edges().get(k);
                final int vA = kEdge.vA().index();
                final int vB = kEdge.vB().index();
                final int rootP = find(vA, state.unionInfo(AbsoluteDirection.Adjacent)[playerUF]);
                final int rootQ = find(vB, state.unionInfo(AbsoluteDirection.Adjacent)[playerUF]);
                if (rootP != rootQ) {
                    if (state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].getGroupSize(rootP) < state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].getGroupSize(rootQ)) {
                        state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].setParent(rootP, rootQ);
                        state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].mergeItemsLists(rootQ, rootP);
                    }
                    else {
                        state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].setParent(rootQ, rootP);
                        state.unionInfo(AbsoluteDirection.Adjacent)[playerUF].mergeItemsLists(rootP, rootQ);
                    }
                }
            }
        }
    }
    
    private static void union(final int siteId, final TIntArrayList validPosition, final boolean libertyFlag, final UnionInfoD uf, final int whoSiteId) {
        final int validPositionSz = validPosition.size();
        uf.setItem(siteId, siteId);
        uf.setParent(siteId, siteId);
        if (libertyFlag) {
            uf.setItemWithOrthoNeighbors(siteId, siteId);
        }
        for (int i = 0; i < validPositionSz; ++i) {
            final int ni = validPosition.getQuick(i);
            boolean connectflag = true;
            if (uf.getParent(ni) != Constants.UNUSED) {
                for (int j = i + 1; j < validPositionSz; ++j) {
                    final int nj = validPosition.getQuick(j);
                    if (connected(ni, nj, uf)) {
                        connectflag = false;
                        break;
                    }
                }
                if (connectflag) {
                    final int rootP = find(ni, uf);
                    final int rootQ = find(siteId, uf);
                    if (rootP == rootQ) {
                        return;
                    }
                    if (uf.getGroupSize(rootP) < uf.getGroupSize(rootQ)) {
                        uf.setParent(rootP, rootQ);
                        uf.mergeItemsLists(rootQ, rootP);
                        if (libertyFlag) {
                            uf.mergeItemWithOrthoNeighbors(rootQ, rootP);
                        }
                    }
                    else {
                        uf.setParent(rootQ, rootP);
                        uf.mergeItemsLists(rootP, rootQ);
                        if (libertyFlag) {
                            uf.mergeItemWithOrthoNeighbors(rootP, rootQ);
                        }
                    }
                }
            }
        }
    }
    
    public static void evalNoLibertyFriendlyD(final Context context, final int deleteId, final AbsoluteDirection dirnChoice) {
        final SiteType type = getSiteType(context);
        final int cid = context.containerId()[deleteId];
        final ContainerState state = context.state().containerStates()[cid];
        final int deletePlayer = state.who(deleteId, type);
        deletion(type, context, deleteId, dirnChoice, false, false, state.unionInfo(dirnChoice)[deletePlayer], deletePlayer);
    }
    
    public static void evalDeletionForBlocking(final Context context, final int deleteId, final AbsoluteDirection dirnChoice) {
        final SiteType type = getSiteType(context);
        final int cid = context.containerId()[deleteId];
        final ContainerState state = context.state().containerStates()[cid];
        final int currentPlayer = state.who(deleteId, type);
        for (int numPlayers = context.game().players().count(), deletePlayer = 1; deletePlayer <= numPlayers; ++deletePlayer) {
            if (currentPlayer != deletePlayer) {
                deletion(type, context, deleteId, dirnChoice, false, true, state.unionInfoBlocking(dirnChoice)[deletePlayer], deletePlayer);
            }
        }
    }
    
    public static TIntArrayList elementIndices(final List<? extends TopologyElement> verticesList) {
        final int verticesListSz = verticesList.size();
        final TIntArrayList integerVerticesList = new TIntArrayList(verticesListSz);
        for (int i = 0; i < verticesListSz; ++i) {
            integerVerticesList.add(verticesList.get(i).index());
        }
        return integerVerticesList;
    }
    
    public static TIntArrayList preProcessingLiberties(final SiteType type, final ContainerState state, final int siteId, final int numPlayer, final TIntArrayList nList, final UnionInfoD uf, final int whoSiteId) {
        final int nListSz = nList.size();
        final TIntArrayList neighbourList = new TIntArrayList(nListSz);
        for (int i = 0; i < nListSz; ++i) {
            final int ni = nList.getQuick(i);
            if ((state.who(ni, type) == whoSiteId && whoSiteId != numPlayer + 1) || (state.who(ni, type) != 0 && whoSiteId == numPlayer + 1)) {
                neighbourList.add(ni);
            }
            else {
                uf.setItemWithOrthoNeighbors(siteId, ni);
            }
        }
        return neighbourList;
    }
    
    private static SiteType getSiteType(final Context context) {
        return (context.game().board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell;
    }
    
    private static boolean connected(final int position1, final int position2, final UnionInfoD uf) {
        final int find1 = find(position1, uf);
        return uf.isSameGroup(find1, position2);
    }
    
    private static int find(final int position, final UnionInfoD uf) {
        final int parentId = uf.getParent(position);
        if (parentId == position || parentId == Constants.UNUSED) {
            return position;
        }
        return find(parentId, uf);
    }
}
