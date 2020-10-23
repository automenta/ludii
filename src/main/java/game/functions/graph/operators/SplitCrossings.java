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
import math.MathRoutines;
import math.Point3D;
import util.Context;

import java.awt.geom.Point2D;

public final class SplitCrossings extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction graphFn;
    private Graph precomputedGraph;
    
    public SplitCrossings(final GraphFunction graph) {
        this.precomputedGraph = null;
        this.graphFn = graph;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph graph = this.graphFn.eval(context, siteType);
        splitAtCrossingPoints(graph);
        splitAtTouchingPoints(graph);
        graph.makeFaces(true);
        graph.resetBasis();
        return graph;
    }
    
    static void splitAtCrossingPoints(final Graph graph) {
        boolean didSplit;
        do {
            didSplit = false;
            for (int ea = 0; ea < graph.edges().size() && !didSplit; ++ea) {
                final Edge edgeA = graph.edges().get(ea);
                final Point3D ptAA = edgeA.vertexA().pt();
                final Point3D ptAB = edgeA.vertexB().pt();
                Vertex pivot = null;
                if (edgeA.vertexA().pivot() != null) {
                    pivot = edgeA.vertexA().pivot();
                }
                if (edgeA.vertexB().pivot() != null) {
                    pivot = edgeA.vertexB().pivot();
                }
                for (int eb = ea + 1; eb < graph.edges().size() && !didSplit; ++eb) {
                    final Edge edgeB = graph.edges().get(eb);
                    final Point3D ptBA = edgeB.vertexA().pt();
                    final Point3D ptBB = edgeB.vertexB().pt();
                    if (pivot == null && edgeB.vertexA().pivot() != null) {
                        pivot = edgeB.vertexA().pivot();
                    }
                    if (pivot == null && edgeB.vertexB().pivot() != null) {
                        pivot = edgeB.vertexB().pivot();
                    }
                    final Point2D ptX = MathRoutines.crossingPoint(ptAA.x(), ptAA.y(), ptAB.x(), ptAB.y(), ptBA.x(), ptBA.y(), ptBB.x(), ptBB.y());
                    if (ptX != null) {
                        final Vertex vertex = graph.addVertex(ptX.getX(), ptX.getY());
                        final int vid = vertex.id();
                        if (pivot != null) {
                            vertex.setPivot(pivot);
                        }
                        graph.removeEdge(eb);
                        graph.removeEdge(ea);
                        graph.findOrAddEdge(edgeA.vertexA().id(), vid);
                        graph.findOrAddEdge(vid, edgeA.vertexB().id());
                        graph.findOrAddEdge(edgeB.vertexA().id(), vid);
                        graph.findOrAddEdge(vid, edgeB.vertexB().id());
                        didSplit = true;
                    }
                }
            }
        } while (didSplit);
    }
    
    static void splitAtTouchingPoints(final Graph graph) {
        boolean didSplit;
        do {
            didSplit = false;
            for (int ea = 0; ea < graph.edges().size() && !didSplit; ++ea) {
                final Edge edgeA = graph.edges().get(ea);
                final Point3D ptAA = edgeA.vertexA().pt();
                final Point3D ptAB = edgeA.vertexB().pt();
                Vertex pivot = null;
                if (edgeA.vertexA().pivot() != null) {
                    pivot = edgeA.vertexA().pivot();
                }
                if (edgeA.vertexB().pivot() != null) {
                    pivot = edgeA.vertexB().pivot();
                }
                for (final Vertex vertex : graph.vertices()) {
                    if (edgeA.contains(vertex)) {
                        continue;
                    }
                    final Point3D ptT = MathRoutines.touchingPoint(vertex.pt(), ptAA, ptAB);
                    if (ptT == null) {
                        continue;
                    }
                    if (pivot != null) {
                        vertex.setPivot(pivot);
                    }
                    graph.removeEdge(ea);
                    graph.findOrAddEdge(edgeA.vertexA().id(), vertex.id());
                    graph.findOrAddEdge(vertex.id(), edgeA.vertexB().id());
                    didSplit = true;
                }
            }
        } while (didSplit);
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
