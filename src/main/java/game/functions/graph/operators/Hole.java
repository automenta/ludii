// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import game.Game;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Poly;
import math.Polygon;
import util.Context;

public final class Hole extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction graphFn;
    private final Polygon polygon;
    private Graph precomputedGraph;
    
    public Hole(final GraphFunction graphFn, final Poly poly) {
        this.precomputedGraph = null;
        this.graphFn = graphFn;
        this.polygon = poly.polygon();
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph graph = this.graphFn.eval(context, siteType);
        if (this.polygon.size() < 3) {
            System.out.println("** Hole: Clip region only has " + this.polygon.size() + " points.");
            return graph;
        }
        this.polygon.inflate(0.1);
        for (int fid = graph.faces().size() - 1; fid >= 0; --fid) {
            if (this.polygon.contains(graph.faces().get(fid).pt2D())) {
                graph.removeFace(fid, true);
            }
        }
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        return this.graphFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        flags |= this.graphFn.gameFlags(game);
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
