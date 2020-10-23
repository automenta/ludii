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

public final class Renumber extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final SiteType siteTypeA;
    private final SiteType siteTypeB;
    private final SiteType siteTypeC;
    private final GraphFunction graphFn;
    private Graph precomputedGraph;
    
    public Renumber(@Opt final SiteType siteTypeA, @Opt final SiteType siteTypeB, @Opt final SiteType siteTypeC, final GraphFunction graph) {
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
        final Graph result = this.graphFn.eval(context, siteType);
        if (result.vertices().isEmpty()) {
            System.out.println("** Rotate.eval(): Rotating empty graph.");
            return result;
        }
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
        if (types.cardinality() == 0) {
            result.reorder();
        }
        else {
            if (types.get(0)) {
                result.reorder(SiteType.Vertex);
            }
            if (types.get(1)) {
                result.reorder(SiteType.Edge);
            }
            if (types.get(2)) {
                result.reorder(SiteType.Cell);
            }
        }
        return result;
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
