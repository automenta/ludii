// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.mesh;

import annotations.Hide;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.GraphElement;
import game.util.graph.Vertex;
import math.MathRoutines;
import math.Polygon;
import util.Context;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;

@Hide
public class CustomOnMesh extends Basis
{
    private static final long serialVersionUID = 1L;
    private final Integer numVertices;
    private final Polygon polygon;
    private final List<Point2D> points;
    
    public CustomOnMesh(final DimFunction numVertices, final Polygon polygon, final List<Point2D> points) {
        this.polygon = new Polygon();
        this.basis = BasisType.Mesh;
        this.shape = ShapeType.Custom;
        this.numVertices = numVertices.eval();
        this.points = points;
        this.polygon.setFrom(polygon);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final Graph graph = new Graph();
        if (this.numVertices == null) {
            for (final Point2D pt : this.points) {
                insertVertex(graph, pt);
            }
        }
        else {
            final Random rng = new Random();
            this.polygon.inflate(0.1);
            final Rectangle2D bounds = this.polygon.bounds();
            int n = 0;
        Label_0091:
            while (n < this.numVertices) {
                int iterations = 0;
                while (++iterations <= 1000) {
                    final double x = bounds.getMinX() + rng.nextDouble() * bounds.getWidth();
                    final double y = bounds.getMinY() + rng.nextDouble() * bounds.getHeight();
                    final Point2D pt2 = new Point2D.Double(x, y);
                    if (this.polygon.contains(pt2)) {
                        insertVertex(graph, pt2);
                        ++n;
                        continue Label_0091;
                    }
                }
                throw new RuntimeException("Couldn't place point in mesh shape.");
            }
        }
        graph.makeFaces(true);
        graph.setBasisAndShape(this.basis, this.shape);
        return graph;
    }
    
    static void insertVertex(final Graph graph, final Point2D pt) {
        final List<? extends GraphElement> vertices = graph.elements(SiteType.Vertex);
        if (vertices.isEmpty()) {
            graph.addVertex(pt);
            return;
        }
        if (vertices.size() == 1) {
            graph.addVertex(pt);
            graph.findOrAddEdge(0, 1);
            return;
        }
        if (vertices.size() == 2) {
            graph.addVertex(pt);
            graph.findOrAddEdge(0, 2);
            graph.findOrAddEdge(1, 2);
            return;
        }
        for (int i = 0; i < vertices.size(); ++i) {
            final Vertex vertexI = (Vertex)vertices.get(i);
            for (int j = i + 1; j < vertices.size(); ++j) {
                if (i != j) {
                    final Vertex vertexJ = (Vertex)vertices.get(j);
                    for (int k = j + 1; k < vertices.size(); ++k) {
                        if (i != k) {
                            if (j != k) {
                                final Vertex vertexK = (Vertex)vertices.get(k);
                                if (MathRoutines.pointInTriangle(pt, vertexI.pt2D(), vertexJ.pt2D(), vertexK.pt2D())) {
                                    insertVertex(graph, pt, vertexI, vertexJ, vertexK);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        double bestDistance = 1000000.0;
        double nextBestDistance = 1000000.0;
        GraphElement bestVertex = null;
        GraphElement nextBestVertex = null;
        for (final GraphElement vertex : vertices) {
            final double dist = MathRoutines.distance(pt, vertex.pt2D());
            if (dist < bestDistance) {
                nextBestDistance = bestDistance;
                nextBestVertex = bestVertex;
                bestDistance = dist;
                bestVertex = vertex;
            }
            else {
                if (dist >= nextBestDistance) {
                    continue;
                }
                nextBestDistance = dist;
                nextBestVertex = vertex;
            }
        }
        final Vertex vertex2 = graph.addVertex(pt);
        graph.findOrAddEdge(vertex2.id(), bestVertex.id());
        graph.findOrAddEdge(vertex2.id(), nextBestVertex.id());
    }
    
    static void insertVertex(final Graph graph, final Point2D pt, final Vertex vertexI, final Vertex vertexJ, final Vertex vertexK) {
        final Vertex vertex = graph.addVertex(pt);
        graph.findOrAddEdge(vertex.id(), vertexI.id());
        graph.findOrAddEdge(vertex.id(), vertexJ.id());
        graph.findOrAddEdge(vertex.id(), vertexK.id());
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
