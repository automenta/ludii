// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import annotations.Opt;
import game.Game;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Graph;
import util.Context;

import java.util.BitSet;

public final class Recoordinate extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final SiteType siteTypeA;
    private final SiteType siteTypeB;
    private final SiteType siteTypeC;
    private final GraphFunction graphFn;
    private Graph precomputedGraph;
    
    public Recoordinate(@Opt final SiteType siteTypeA, @Opt final SiteType siteTypeB, @Opt final SiteType siteTypeC, final GraphFunction graph) {
        this.precomputedGraph = null;
        this.graphFn = graph;
        this.siteTypeA = siteTypeA;
        this.siteTypeB = siteTypeB;
        this.siteTypeC = siteTypeC;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph graph = this.graphFn.eval(context, siteType);
        final BitSet types = new BitSet();
        if (this.siteTypeA != null) {
            types.set(this.siteTypeA.ordinal());
        }
        if (this.siteTypeB != null) {
            types.set(this.siteTypeB.ordinal());
        }
        if (this.siteTypeC != null) {
            types.set(this.siteTypeC.ordinal());
        }
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        return this.graphFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = this.graphFn.gameFlags(game);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.graphFn.preprocess(game);
        if (this.isStatic()) {
            this.precomputedGraph = this.eval(new Context(game, null), (game.board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell);
        }
    }
}
