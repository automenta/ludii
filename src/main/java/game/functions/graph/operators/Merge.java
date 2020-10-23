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
import game.util.graph.Edge;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import main.math.MathRoutines;
import main.math.Vector;
import util.Context;

import java.util.ArrayList;
import java.util.List;

public final class Merge extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction[] graphFns;
    private final boolean connect;
    private Graph precomputedGraph;
    
    public Merge(final GraphFunction graphA, final GraphFunction graphB, @Opt @Name final Boolean connect) {
        this.precomputedGraph = null;
        (this.graphFns = new GraphFunction[2])[0] = graphA;
        this.graphFns[1] = graphB;
        this.connect = (connect != null && connect);
    }
    
    public Merge(final GraphFunction[] graphs, @Opt @Name final Boolean connect) {
        this.precomputedGraph = null;
        this.graphFns = graphs;
        this.connect = (connect != null && connect);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph[] graphs = new Graph[this.graphFns.length];
        for (int n = 0; n < this.graphFns.length; ++n) {
            final GraphFunction fn = this.graphFns[n];
            graphs[n] = fn.eval(context, siteType);
        }
        final List<Vertex> vertices = new ArrayList<>();
        final List<Edge> edges = new ArrayList<>();
        int offset = 0;
        for (final Graph subGraph : graphs) {
            for (final Vertex vertex : subGraph.vertices()) {
                final Vertex newVertex = new Vertex(vertices.size(), vertex.pt().x(), vertex.pt().y(), vertex.pt().z());
                newVertex.setTilingAndShape(vertex.basis(), vertex.shape());
                if (vertex.pivot() != null) {
                    newVertex.setPivot(vertex.pivot());
                }
                vertices.add(newVertex);
            }
            for (final Edge edge : subGraph.edges()) {
                final Vertex va = vertices.get(edge.vertexA().id() + offset);
                final Vertex vb = vertices.get(edge.vertexB().id() + offset);
                final Edge newEdge = new Edge(edges.size(), va, vb);
                newEdge.setTilingAndShape(edge.basis(), edge.shape());
                if (edge.tangentA() != null) {
                    newEdge.setTangentA(new Vector(edge.tangentA()));
                }
                if (edge.tangentB() != null) {
                    newEdge.setTangentB(new Vector(edge.tangentB()));
                }
                edges.add(newEdge);
            }
            offset += subGraph.vertices().size();
        }
        mergeVertices(vertices, edges);
        game.util.graph.Graph.removeDuplicateEdges(edges);
        final Graph graph = new Graph(vertices, edges);
        if (this.connect) {
            final double threshold = 1.1 * graph.averageEdgeLength();
            for (final Vertex vertexA : graph.vertices()) {
                for (final Vertex vertexB : graph.vertices()) {
                    if (vertexA.id() == vertexB.id()) {
                        continue;
                    }
                    if (MathRoutines.distance(vertexA.pt2D(), vertexB.pt2D()) >= threshold) {
                        continue;
                    }
                    graph.findOrAddEdge(vertexA.id(), vertexB.id());
                }
            }
            graph.makeFaces(true);
        }
        graph.resetBasis();
        graph.resetShape();
        return graph;
    }
    
    static void mergeVertices(final List<Vertex> vertices, final List<Edge> edges) {
        final double tolerance = 0.01;
        for (int v = 0; v < vertices.size(); ++v) {
            final Vertex base = vertices.get(v);
            for (int vv = vertices.size() - 1; vv > v; --vv) {
                final Vertex other = vertices.get(vv);
                if (base.coincident(other, 0.01)) {
                    mergeVertices(vertices, edges, v, vv);
                }
            }
        }
    }
    
    static void mergeVertices(final List<Vertex> vertices, final List<Edge> edges, final int vid, final int coincidentId) {
        final Vertex survivor = vertices.get(vid);
        for (final Vertex vertex : vertices) {
            if (vertex.pivot() != null && vertex.pivot().id() == coincidentId) {
                vertex.setPivot(survivor);
            }
        }
        for (final Edge edge : edges) {
            if (edge.vertexA().id() == coincidentId) {
                edge.setVertexA(survivor);
            }
            if (edge.vertexB().id() == coincidentId) {
                edge.setVertexB(survivor);
            }
        }
        for (int n = coincidentId + 1; n < vertices.size(); ++n) {
            final Vertex vertexN = vertices.get(n);
            vertexN.setId(vertexN.id() - 1);
        }
        vertices.remove(coincidentId);
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
    
    public class PivotPair
    {
        public int id;
        public int pivotId;
        
        public PivotPair(final int id, final int pivotId) {
            this.id = id;
            this.pivotId = pivotId;
        }
    }
}
