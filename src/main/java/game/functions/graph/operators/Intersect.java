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
import game.util.graph.Vertex;
import util.Context;

import java.util.ArrayList;
import java.util.List;

public final class Intersect extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction[] graphFns;
    private Graph precomputedGraph;
    
    public Intersect(final GraphFunction graphA, final GraphFunction graphB) {
        this.precomputedGraph = null;
        (this.graphFns = new GraphFunction[2])[0] = graphA;
        this.graphFns[1] = graphB;
    }
    
    public Intersect(final GraphFunction[] graphs) {
        this.precomputedGraph = null;
        this.graphFns = graphs;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final int numGraphs = this.graphFns.length;
        final Graph[] graphs = new Graph[numGraphs];
        for (int n = 0; n < numGraphs; ++n) {
            final GraphFunction fn = this.graphFns[n];
            graphs[n] = fn.eval(context, siteType);
        }
        if (numGraphs == 1) {
            return graphs[0];
        }
        final double tolerance = 0.01;
        final List<Vertex> vertices = new ArrayList<>();
        final List<Edge> edges = new ArrayList<>();
        for (final Vertex vertex : graphs[0].vertices()) {
            final Vertex newVertex = new Vertex(vertices.size(), vertex.pt());
            newVertex.setTilingAndShape(vertex.basis(), vertex.shape());
            vertices.add(newVertex);
        }
        for (final Edge edge : graphs[0].edges()) {
            final Vertex va = vertices.get(edge.vertexA().id());
            final Vertex vb = vertices.get(edge.vertexB().id());
            final Edge newEdge = new Edge(edges.size(), va, vb);
            newEdge.setTilingAndShape(edge.basis(), edge.shape());
            edges.add(newEdge);
        }
        for (int e = edges.size() - 1; e >= 0; --e) {
            final Edge edge = edges.get(e);
            boolean foundInAll = true;
            for (int g = 1; g < numGraphs; ++g) {
                boolean found = false;
                for (final Edge edgeG : graphs[g].edges()) {
                    if (edge.coincidentVertices(edgeG, 0.01)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    foundInAll = false;
                    break;
                }
            }
            if (!foundInAll) {
                edges.remove(e);
            }
        }
        for (int v = vertices.size() - 1; v >= 0; --v) {
            final Vertex vertex = vertices.get(v);
            boolean foundInAll = true;
            for (int g = 1; g < numGraphs; ++g) {
                boolean found = false;
                for (final Vertex vertexG : graphs[g].vertices()) {
                    if (vertex.coincident(vertexG, 0.01)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    foundInAll = false;
                    break;
                }
            }
            if (!foundInAll) {
                vertices.remove(v);
            }
        }
        for (int v = 0; v < vertices.size(); ++v) {
            vertices.get(v).setId(v);
        }
        for (int e = 0; e < edges.size(); ++e) {
            edges.get(e).setId(e);
        }
        final Graph graph = new Graph(vertices, edges);
        graph.resetBasis();
        graph.resetShape();
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        for (final GraphFunction fn : this.graphFns) {
            if (!fn.isStatic()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        for (final GraphFunction fn : this.graphFns) {
            flags |= fn.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        for (final GraphFunction fn : this.graphFns) {
            fn.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedGraph = this.eval(new Context(game, null), (game.board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell);
        }
    }
}
