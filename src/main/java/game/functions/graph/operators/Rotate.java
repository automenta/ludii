// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import game.Game;
import game.functions.floats.FloatFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Graph;
import util.Context;

public final class Rotate extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final FloatFunction degreesFn;
    private final GraphFunction graphFn;
    private Graph precomputedGraph;
    
    public Rotate(final FloatFunction degreesFn, final GraphFunction graph) {
        this.precomputedGraph = null;
        this.degreesFn = degreesFn;
        this.graphFn = graph;
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
        final double degrees = this.degreesFn.eval(context);
        graph.rotate(degrees);
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        return this.graphFn.isStatic() && this.degreesFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = this.graphFn.gameFlags(game) | this.degreesFn.gameFlags(game);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.graphFn.preprocess(game);
        this.degreesFn.preprocess(game);
        if (this.isStatic()) {
            this.precomputedGraph = this.eval(new Context(game, null), (game.board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell);
        }
    }
}
