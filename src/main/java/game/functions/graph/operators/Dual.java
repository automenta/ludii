// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import game.Game;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Edge;
import game.util.graph.Face;
import game.util.graph.Graph;
import util.Context;

public final class Dual extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction graphFn;
    private Graph precomputedGraph;
    
    public Dual(final GraphFunction graph) {
        this.precomputedGraph = null;
        this.graphFn = graph;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph source = this.graphFn.eval(context, siteType);
        if (source.vertices().isEmpty() || source.edges().isEmpty() || source.faces().isEmpty()) {
            System.out.println("** Dual.eval(): Taking dual of graph with no vertices, edges or faces.");
            return source;
        }
        final Graph graph = new Graph();
        for (final Face face : source.faces()) {
            graph.addVertex(face.pt());
        }
        if (graph.vertices().isEmpty()) {
            return graph;
        }
        for (final Edge edge : source.edges()) {
            if (edge.left() != null && edge.right() != null) {
                graph.addEdge(edge.left().id(), edge.right().id());
            }
        }
        graph.makeFaces(false);
        graph.setBasisAndShape(BasisType.Dual, ShapeType.NoShape);
        graph.reorder();
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
