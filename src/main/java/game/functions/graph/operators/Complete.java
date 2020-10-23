// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Face;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import util.Context;

public final class Complete extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction graphFn;
    private Graph precomputedGraph;
    private final boolean eachCell;
    
    public Complete(final GraphFunction graph, @Opt @Name final Boolean eachCell) {
        this.precomputedGraph = null;
        this.graphFn = graph;
        this.eachCell = (eachCell != null && eachCell);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph graph = this.graphFn.eval(context, siteType);
        if (this.eachCell) {
            for (final Face face : graph.faces()) {
                for (int va = 0; va < face.vertices().size(); ++va) {
                    final Vertex vertexA = face.vertices().get(va);
                    for (int vb = va + 1; vb < face.vertices().size(); ++vb) {
                        final Vertex vertexB = face.vertices().get(vb);
                        graph.findOrAddEdge(vertexA, vertexB);
                    }
                }
            }
        }
        else {
            graph.clear(SiteType.Edge);
            for (int va2 = 0; va2 < graph.vertices().size(); ++va2) {
                for (int vb2 = va2 + 1; vb2 < graph.vertices().size(); ++vb2) {
                    graph.findOrAddEdge(va2, vb2);
                }
            }
            graph.makeFaces(true);
        }
        graph.resetBasis();
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        return this.graphFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = this.graphFn.gameFlags(game);
        if (game.board().defaultSite() != SiteType.Cell) {
            flags |= 0x800000L;
        }
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
