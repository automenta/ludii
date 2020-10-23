// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.*;
import math.Polygon;
import util.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Remove extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction graphFn;
    private final Polygon polygon;
    private final Float[][][] facePositions;
    private final Float[][][] edgePositions;
    private final Float[][] vertexPositions;
    private final DimFunction[] faceIndices;
    private final DimFunction[][] edgeIndices;
    private final DimFunction[] vertexIndices;
    private final boolean trimEdges;
    private Graph precomputedGraph;
    
    public Remove(final GraphFunction graph, @Opt @Or @Name final Float[][][] cells, @Opt @Or @Name final DimFunction[] Cells, @Opt @Or2 @Name final Float[][][] edges, @Opt @Or2 @Name final DimFunction[][] Edges, @Opt @Or @Name final Float[][] vertices, @Opt @Or @Name final DimFunction[] Vertices, @Opt @Name final Boolean trimEdges) {
        this.precomputedGraph = null;
        int numNonNullF = 0;
        if (cells != null) {
            ++numNonNullF;
        }
        if (Cells != null) {
            ++numNonNullF;
        }
        if (numNonNullF > 1) {
            throw new IllegalArgumentException("Only one 'cell' parameter can be non-null.");
        }
        int numNonNullE = 0;
        if (edges != null) {
            ++numNonNullE;
        }
        if (Edges != null) {
            ++numNonNullE;
        }
        if (numNonNullE > 1) {
            throw new IllegalArgumentException("Only one 'edge' parameter can be non-null.");
        }
        int numNonNullV = 0;
        if (vertices != null) {
            ++numNonNullV;
        }
        if (Vertices != null) {
            ++numNonNullV;
        }
        if (numNonNullV > 1) {
            throw new IllegalArgumentException("Only one 'vertex' parameter can be non-null.");
        }
        this.graphFn = graph;
        this.polygon = null;
        this.facePositions = cells;
        this.edgePositions = edges;
        this.vertexPositions = vertices;
        this.faceIndices = Cells;
        this.edgeIndices = Edges;
        this.vertexIndices = Vertices;
        this.trimEdges = (trimEdges == null || trimEdges);
    }
    
    public Remove(final GraphFunction graphFn, final Poly poly, @Opt @Name final Boolean trimEdges) {
        this.precomputedGraph = null;
        this.graphFn = graphFn;
        this.polygon = poly.polygon();
        this.facePositions = null;
        this.edgePositions = null;
        this.vertexPositions = null;
        this.faceIndices = null;
        this.edgeIndices = null;
        this.vertexIndices = null;
        this.trimEdges = (trimEdges == null || trimEdges);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final double tolerance = 0.01;
        final Graph graph = this.graphFn.eval(context, siteType);
        if (this.polygon != null) {
            for (int vid = graph.vertices().size() - 1; vid >= 0; --vid) {
                final Vertex vertex = graph.vertices().get(vid);
                if (this.polygon.contains(vertex.pt2D())) {
                    graph.removeVertex(vertex);
                }
            }
        }
        if (this.facePositions != null) {
            for (final Float[][] pts : this.facePositions) {
                final int[] vertIds = new int[pts.length];
                for (int n = 0; n < pts.length; ++n) {
                    if (pts[n].length < 2) {
                        System.out.println("** Remove: Two values expected for vertex.");
                    }
                    else {
                        final double x = pts[n][0];
                        final double y = pts[n][1];
                        final double z = (pts[n].length > 2) ? pts[n][2] : 0.0;
                        final Vertex vertex2 = graph.findVertex(x, y, z, 0.01);
                        if (vertex2 == null) {
                            System.out.println("** Couldn't find face vertex.");
                            vertIds[n] = -1;
                        }
                        else {
                            vertIds[n] = vertex2.id();
                        }
                    }
                }
                final Face face = graph.findFace(vertIds);
                if (face != null) {
                    graph.remove(face, this.trimEdges);
                }
                else {
                    System.out.println("** Face not found from vertices.");
                }
            }
        }
        else if (this.faceIndices != null) {
            final List<Integer> list = new ArrayList<>();
            for (final DimFunction id : this.faceIndices) {
                list.add(id.eval());
            }
            Collections.sort(list);
            Collections.reverse(list);
            for (final Integer id2 : list) {
                graph.removeFace(id2, this.trimEdges);
            }
        }
        if (this.edgePositions != null) {
            for (final Float[][] pts : this.edgePositions) {
                final double ax = pts[0][0];
                final double ay = pts[0][1];
                final double az = (pts[0].length > 2) ? pts[0][2] : 0.0;
                final double bx = pts[1][0];
                final double by = pts[1][1];
                final double bz = (pts[1].length > 2) ? pts[1][2] : 0.0;
                final Vertex vertexA = graph.findVertex(ax, ay, az, 0.01);
                final Vertex vertexB = graph.findVertex(bx, by, bz, 0.01);
                if (vertexA != null && vertexB != null) {
                    final Edge edge = graph.findEdge(vertexA, vertexB);
                    if (edge != null) {
                        graph.remove(edge, false);
                    }
                    else {
                        System.out.println("** Edge vertices not found.");
                    }
                }
                else {
                    System.out.println("** Edge vertices not found.");
                }
            }
        }
        else if (this.edgeIndices != null) {
            for (final DimFunction[] vids : this.edgeIndices) {
                if (vids.length == 2) {
                    graph.removeEdge(vids[0].eval(), vids[1].eval());
                }
            }
        }
        if (this.vertexPositions != null) {
            for (final Float[] pt : this.vertexPositions) {
                final double x2 = pt[0];
                final double y2 = pt[1];
                final double z2 = (pt.length > 2) ? pt[2] : 0.0;
                final Vertex vertex3 = graph.findVertex(x2, y2, z2, 0.01);
                if (vertex3 != null) {
                    graph.removeVertex(vertex3);
                }
            }
        }
        else if (this.vertexIndices != null) {
            final List<Integer> list = new ArrayList<>();
            for (final DimFunction id : this.vertexIndices) {
                list.add(id.eval());
            }
            Collections.sort(list);
            Collections.reverse(list);
            for (final Integer id2 : list) {
                graph.removeVertex(id2);
            }
        }
        graph.resetShape();
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        final boolean isStatic = true;
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = 0L;
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.isStatic()) {
            this.precomputedGraph = this.eval(new Context(game, null), (game.board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell);
        }
    }
}
