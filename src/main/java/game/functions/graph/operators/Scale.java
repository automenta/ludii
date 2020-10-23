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

public final class Scale extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final FloatFunction scaleXFn;
    private final FloatFunction scaleYFn;
    private final FloatFunction scaleZFn;
    private final GraphFunction graphFn;
    private Graph precomputedGraph;
    
    public Scale(final FloatFunction scaleX, @Opt final FloatFunction scaleY, @Opt final FloatFunction scaleZ, final GraphFunction graph) {
        this.precomputedGraph = null;
        this.graphFn = graph;
        this.scaleXFn = scaleX;
        this.scaleYFn = scaleY;
        this.scaleZFn = scaleZ;
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
        final double sx = this.scaleXFn.eval(context);
        final double sy = (this.scaleYFn != null) ? this.scaleYFn.eval(context) : this.scaleXFn.eval(context);
        final double sz = (this.scaleZFn != null) ? this.scaleZFn.eval(context) : 1.0;
        graph.scale(sx, sy, sz);
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        return this.graphFn.isStatic() && this.scaleXFn.isStatic() && this.scaleYFn.isStatic() && this.scaleZFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = this.graphFn.gameFlags(game) | this.scaleXFn.gameFlags(game) | this.scaleYFn.gameFlags(game) | this.scaleZFn.gameFlags(game);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.graphFn.preprocess(game);
        this.scaleXFn.preprocess(game);
        this.scaleYFn.preprocess(game);
        this.scaleZFn.preprocess(game);
        if (this.isStatic()) {
            this.precomputedGraph = this.eval(new Context(game, null), (game.board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell);
        }
    }
}
