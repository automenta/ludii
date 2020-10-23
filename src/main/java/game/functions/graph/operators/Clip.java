// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import game.Game;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Edge;
import game.util.graph.Graph;
import game.util.graph.Poly;
import game.util.graph.Vertex;
import main.math.Polygon;
import util.Context;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public final class Clip extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction graphFn;
    private final Polygon polygon;
    private Graph precomputedGraph;
    
    public Clip(final GraphFunction graphFn, final Poly poly) {
        this.precomputedGraph = null;
        this.graphFn = graphFn;
        this.polygon = poly.polygon();
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph source = this.graphFn.eval(context, siteType);
        if (this.polygon.size() < 3) {
            System.out.println("** Clip region only has " + this.polygon.size() + " points.");
            return source;
        }
        this.polygon.inflate(0.1);
        final List<Vertex> vertices = new ArrayList<>();
        final List<Edge> edges = new ArrayList<>();
        final BitSet remove = new BitSet();
        for (final Vertex vertex : source.vertices()) {
            final Vertex newVertex = new Vertex(vertices.size(), vertex.pt());
            newVertex.setTilingAndShape(vertex.basis(), vertex.shape());
            vertices.add(newVertex);
            if (this.polygon.contains(vertex.pt2D())) {
                remove.set(vertex.id(), true);
            }
        }
        for (final Edge edge : source.edges()) {
            final Vertex va = vertices.get(edge.vertexA().id());
            final Vertex vb = vertices.get(edge.vertexB().id());
            if (!remove.get(va.id()) && !remove.get(vb.id())) {
                final Edge newEdge = new Edge(edges.size(), va, vb);
                newEdge.setTilingAndShape(edge.basis(), edge.shape());
                edges.add(newEdge);
            }
        }
        for (int v = vertices.size() - 1; v >= 0; --v) {
            if (remove.get(v)) {
                vertices.remove(v);
            }
        }
        for (int v = 0; v < vertices.size(); ++v) {
            vertices.get(v).setId(v);
        }
        final Graph graph = new Graph(vertices, edges);
        graph.resetShape();
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
