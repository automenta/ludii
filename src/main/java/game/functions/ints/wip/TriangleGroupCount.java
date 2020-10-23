// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.wip;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.last.LastTo;
import game.types.board.SiteType;
import game.types.play.RoleType;
import gnu.trove.list.array.TIntArrayList;
import topology.Edge;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class TriangleGroupCount extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final RoleType who;
    private final SiteType indexType;
    private final IntFunction startFn;
    
    public TriangleGroupCount(final SiteType type, final RoleType who, @Opt final IntFunction start) {
        this.indexType = type;
        this.who = who;
        this.startFn = ((start == null) ? new LastTo(null) : start);
    }
    
    @Override
    public int eval(final Context context) {
        final int siteId = this.startFn.eval(context);
        final ContainerState state = context.state().containerStates()[0];
        final Topology graph = context.topology();
        final Edge edge = graph.edges().get(siteId);
        final int whoSiteId = new Id(null, this.who).eval(context);
        final TIntArrayList resultA = new TIntArrayList();
        final TIntArrayList resultB = new TIntArrayList();
        for (final Edge edge2 : edge.vA().edges()) {
            if (edge2.index() != edge.index() && state.who(edge2.index(), this.indexType) == whoSiteId) {
                for (final Edge edge3 : edge2.vA().edges()) {
                    if (edge3.index() != edge2.index() && state.who(edge3.index(), this.indexType) == whoSiteId && edge3 != edge) {
                        resultA.add(edge3.index());
                    }
                }
                for (final Edge edge3 : edge2.vB().edges()) {
                    if (edge3.index() != edge2.index() && state.who(edge3.index(), this.indexType) == whoSiteId) {
                        resultA.add(edge3.index());
                    }
                }
            }
        }
        for (final Edge edge2 : edge.vB().edges()) {
            if (edge2.index() != edge.index() && state.who(edge2.index(), this.indexType) == whoSiteId) {
                resultB.add(edge2.index());
            }
        }
        final TIntArrayList common = commonItems(resultA, resultB);
        if (common.size() != 1) {
            return common.size();
        }
        final TIntArrayList resultA2 = new TIntArrayList();
        final TIntArrayList resultB2 = new TIntArrayList();
        final Edge firstEdge = edge;
        final Edge secondEdge = graph.edges().get(common.get(0));
        final int count = 1;
        for (final Edge edge4 : firstEdge.vA().edges()) {
            if (edge4.index() != firstEdge.index() && state.who(edge4.index(), this.indexType) == whoSiteId) {
                resultA2.add(edge4.index());
            }
        }
        for (final Edge edge4 : firstEdge.vB().edges()) {
            if (edge4.index() != firstEdge.index() && state.who(edge4.index(), this.indexType) == whoSiteId) {
                resultA2.add(edge4.index());
            }
        }
        for (final Edge edge4 : secondEdge.vA().edges()) {
            if (edge4.index() != secondEdge.index() && state.who(edge4.index(), this.indexType) == whoSiteId) {
                resultB2.add(edge4.index());
            }
        }
        for (final Edge edge4 : secondEdge.vB().edges()) {
            if (edge4.index() != secondEdge.index() && state.who(edge4.index(), this.indexType) == whoSiteId) {
                resultB2.add(edge4.index());
            }
        }
        final TIntArrayList common2 = commonItems(resultA2, resultB2);
        final Edge thirdEdge = graph.edges().get(common2.get(0));
        if (moreTriangle(context, this.indexType, secondEdge, edge, thirdEdge, whoSiteId)) {
            return 2;
        }
        if (moreTriangle(context, this.indexType, thirdEdge, secondEdge, edge, whoSiteId)) {
            return 2;
        }
        return 1;
    }
    
    public static boolean moreTriangle(final Context context, final SiteType indexType, final Edge selectedEdge, final Edge firstEdge, final Edge secondEdge, final int whoSiteId) {
        final ContainerState state = context.state().containerStates()[0];
        final TIntArrayList resultA = new TIntArrayList();
        final TIntArrayList resultB = new TIntArrayList();
        for (final Edge edge2 : selectedEdge.vA().edges()) {
            if (edge2.index() != selectedEdge.index() && state.who(edge2.index(), indexType) == whoSiteId) {
                for (final Edge edge3 : edge2.vA().edges()) {
                    if (edge3.index() != edge2.index() && state.who(edge3.index(), indexType) == whoSiteId && edge3 != selectedEdge) {
                        resultA.add(edge3.index());
                    }
                }
                for (final Edge edge3 : edge2.vB().edges()) {
                    if (edge3.index() != edge2.index() && state.who(edge3.index(), indexType) == whoSiteId) {
                        resultA.add(edge3.index());
                    }
                }
            }
        }
        for (final Edge edge2 : selectedEdge.vB().edges()) {
            if (edge2.index() != selectedEdge.index() && state.who(edge2.index(), indexType) == whoSiteId && edge2 != firstEdge && edge2 != secondEdge) {
                resultB.add(edge2.index());
            }
        }
        final TIntArrayList common = commonItems(resultA, resultB);
        return !common.isEmpty();
    }
    
    public static TIntArrayList commonItems(final TIntArrayList list1, final TIntArrayList list2) {
        final TIntArrayList list3 = new TIntArrayList();
        for (int i = 0; i < list1.size(); ++i) {
            for (int j = 0; j < list2.size(); ++j) {
                if (list1.getQuick(i) == list2.getQuick(j)) {
                    list3.add(list1.getQuick(i));
                }
            }
        }
        return list3;
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
