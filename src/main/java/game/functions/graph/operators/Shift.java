// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import annotations.Opt;
import game.Game;
import game.functions.floats.FloatFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Graph;
import util.Context;

public final class Shift extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final FloatFunction dxFn;
    private final FloatFunction dyFn;
    private final FloatFunction dzFn;
    private final GraphFunction graphFn;
    private Graph precomputedGraph;
    
    public Shift(final FloatFunction dx, final FloatFunction dy, @Opt final FloatFunction dz, final GraphFunction graph) {
        this.precomputedGraph = null;
        this.graphFn = graph;
        this.dxFn = dx;
        this.dyFn = dy;
        this.dzFn = dz;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph graph = this.graphFn.eval(context, siteType);
        if (graph.vertices().isEmpty()) {
            System.out.println("** Rotate.eval(): Rotating empty graph.");
            return graph;
        }
        final double dx = this.dxFn.eval(context);
        final double dy = this.dyFn.eval(context);
        final double dz = (this.dzFn != null) ? this.dzFn.eval(context) : 0.0;
        graph.translate(dx, dy, dz);
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        return this.graphFn.isStatic() && this.dxFn.isStatic() && this.dyFn.isStatic() && this.dzFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = this.graphFn.gameFlags(game) | this.dxFn.gameFlags(game) | this.dyFn.gameFlags(game) | this.dzFn.gameFlags(game);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.graphFn.preprocess(game);
        this.dxFn.preprocess(game);
        this.dyFn.preprocess(game);
        if (this.isStatic()) {
            this.precomputedGraph = this.eval(new Context(game, null), (game.board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell);
        }
    }
}
