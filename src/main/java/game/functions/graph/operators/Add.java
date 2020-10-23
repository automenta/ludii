// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.operators;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.floats.FloatFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.types.board.SiteType;
import game.util.graph.Edge;
import game.util.graph.Graph;
import game.util.graph.GraphElement;
import game.util.graph.Vertex;
import main.math.MathRoutines;
import main.math.Point3D;
import main.math.Vector;
import util.Context;

import java.util.ArrayList;
import java.util.List;

public final class Add extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final GraphFunction graphFn;
    private final FloatFunction[][] vertexFns;
    private final FloatFunction[][][] edgeFns;
    private final FloatFunction[][][] edgeCurvedFns;
    private final FloatFunction[][][] faceFns;
    private final DimFunction[][] edgeIndexFns;
    private final DimFunction[][] faceIndexFns;
    private final boolean connect;
    private Graph precomputedGraph;
    
    public Add(@Opt final GraphFunction graph, @Opt @Name final FloatFunction[][] vertices, @Opt @Or @Name final FloatFunction[][][] edges, @Opt @Or @Name final DimFunction[][] Edges, @Opt @Name final FloatFunction[][][] edgesCurved, @Opt @Or @Name final FloatFunction[][][] cells, @Opt @Or @Name final DimFunction[][] Cells, @Opt @Name final Boolean connect) {
        this.precomputedGraph = null;
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
        int numNonNullF = 0;
        if (cells != null) {
            ++numNonNullF;
        }
        if (Cells != null) {
            ++numNonNullF;
        }
        if (numNonNullF > 1) {
            throw new IllegalArgumentException("Only one 'face' parameter can be non-null.");
        }
        this.graphFn = graph;
        this.vertexFns = vertices;
        this.edgeFns = edges;
        this.faceFns = cells;
        this.edgeCurvedFns = edgesCurved;
        this.edgeIndexFns = Edges;
        this.faceIndexFns = Cells;
        this.connect = (connect != null && connect);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final double tolerance = 0.01;
        if (this.precomputedGraph != null) {
            return this.precomputedGraph;
        }
        final Graph graph = (this.graphFn == null) ? new Graph() : this.graphFn.eval(context, siteType);
        final List<Point3D> vertices = new ArrayList<>();
        if (this.vertexFns != null) {
            for (int v = 0; v < this.vertexFns.length; ++v) {
                final FloatFunction[] fns = this.vertexFns[v];
                if (fns.length < 2) {
                    System.out.println("** Add.eval(): Two or three values expected for vertex " + v + ".");
                }
                else {
                    final double x = fns[0].eval(context);
                    final double y = fns[1].eval(context);
                    final double z = (fns.length > 2) ? fns[2].eval(context) : 0.0;
                    vertices.add(new Point3D(x, y, z));
                }
            }
        }
        final List<List<Point3D>> edges = new ArrayList<>();
        if (this.edgeFns != null) {
            for (int e = 0; e < this.edgeFns.length; ++e) {
                final FloatFunction[][] fns2 = this.edgeFns[e];
                if (fns2.length != 2) {
                    System.out.println("** Add.eval(): Two vertex definitions expected for edge " + e + ".");
                }
                else if (fns2[0].length < 2) {
                    System.out.println("** Add.eval(): Two values expcted for vertex A for edge " + e + ".");
                }
                else if (fns2[1].length < 2) {
                    System.out.println("** Add.eval(): Two values expcted for vertex B for edge " + e + ".");
                }
                else {
                    final double ax = fns2[0][0].eval(context);
                    final double ay = fns2[0][1].eval(context);
                    final double az = (fns2[0].length > 2) ? fns2[0][2].eval(context) : 0.0;
                    final double bx = fns2[1][0].eval(context);
                    final double by = fns2[1][1].eval(context);
                    final double bz = (fns2[1].length > 2) ? fns2[1][2].eval(context) : 0.0;
                    final List<Point3D> vertexPair = new ArrayList<>();
                    vertexPair.add(new Point3D(ax, ay, az));
                    vertexPair.add(new Point3D(bx, by, bz));
                    edges.add(vertexPair);
                }
            }
        }
        if (this.edgeCurvedFns != null) {
            for (int e = 0; e < this.edgeCurvedFns.length; ++e) {
                final FloatFunction[][] fns2 = this.edgeCurvedFns[e];
                if (fns2.length != 4) {
                    System.out.println("** Add.eval(): Four points expected for curved edge " + e + ".");
                }
                else if (fns2[0].length < 2) {
                    System.out.println("** Add.eval(): Two values expcted for vertex A for edge " + e + ".");
                }
                else if (fns2[1].length < 2) {
                    System.out.println("** Add.eval(): Two or three values expcted for vertex B for edge " + e + ".");
                }
                else if (fns2[2].length != 2) {
                    System.out.println("** Add.eval(): Two or three values expcted for tangent A for edge " + e + ".");
                }
                else if (fns2[3].length != 2) {
                    System.out.println("** Add.eval(): Two values expcted for tangent B for edge " + e + ".");
                }
                else {
                    final double ax = fns2[0][0].eval(context);
                    final double ay = fns2[0][1].eval(context);
                    final double az = (fns2[0].length > 2) ? fns2[0][2].eval(context) : 0.0;
                    final double bx = fns2[1][0].eval(context);
                    final double by = fns2[1][1].eval(context);
                    final double bz = (fns2[1].length > 2) ? fns2[1][2].eval(context) : 0.0;
                    final double tax = fns2[2][0].eval(context);
                    final double tay = fns2[2][1].eval(context);
                    final double tbx = fns2[3][0].eval(context);
                    final double tby = fns2[3][1].eval(context);
                    final Vector tangentA = new Vector(tax, tay);
                    final Vector tangentB = new Vector(tbx, tby);
                    final Vertex vertexA = graph.findOrAddVertex(ax, ay, az, 0.01);
                    final Vertex vertexB = graph.findOrAddVertex(bx, by, bz, 0.01);
                    graph.addEdge(vertexA, vertexB, tangentA, tangentB);
                }
            }
        }
        final List<List<Point3D>> faces = new ArrayList<>();
        if (this.faceFns != null) {
            for (int f = 0; f < this.faceFns.length; ++f) {
                final FloatFunction[][] fns3 = this.faceFns[f];
                final List<Point3D> face = new ArrayList<>();
                for (int v2 = 0; v2 < fns3.length; ++v2) {
                    final double x2 = fns3[v2][0].eval(context);
                    final double y2 = fns3[v2][1].eval(context);
                    final double z2 = (fns3[v2].length > 2) ? fns3[v2][2].eval(context) : 0.0;
                    face.add(new Point3D(x2, y2, z2));
                }
                faces.add(face);
            }
        }
        final List<Vertex> newVertices = new ArrayList<>();
        for (final Point3D pt : vertices) {
            final Vertex vertex = graph.findVertex(pt, 0.01);
            if (vertex != null) {
                System.out.println("** Duplicate vertex found - not adding.");
            }
            else {
                newVertices.add(graph.addVertex(pt));
            }
        }
        for (final List<Point3D> edgePts : edges) {
            final Point3D ptA = edgePts.get(0);
            final Point3D ptB = edgePts.get(1);
            Vertex vertexA2 = graph.findVertex(ptA, 0.01);
            if (vertexA2 == null) {
                graph.addVertex(ptA);
                vertexA2 = graph.vertices().get(graph.vertices().size() - 1);
            }
            Vertex vertexB2 = graph.findVertex(ptB, 0.01);
            if (vertexB2 == null) {
                graph.addVertex(ptB);
                vertexB2 = graph.vertices().get(graph.vertices().size() - 1);
            }
            graph.findOrAddEdge(vertexA2.id(), vertexB2.id());
        }
        for (final List<Point3D> facePts : faces) {
            final int[] vertIds = new int[facePts.size()];
            for (int n = 0; n < facePts.size(); ++n) {
                final Point3D pt2 = facePts.get(n);
                Vertex vertex2 = graph.findVertex(pt2, 0.01);
                if (vertex2 == null) {
                    graph.addVertex(pt2);
                    vertex2 = graph.vertices().get(graph.vertices().size() - 1);
                }
                vertIds[n] = vertex2.id();
            }
            for (int n = 0; n < vertIds.length; ++n) {
                final int vidA = vertIds[n];
                final int vidB = vertIds[(n + 1) % vertIds.length];
                final Edge edge = graph.findEdge(vidA, vidB);
                if (edge == null) {
                    graph.findOrAddEdge(vidA, vidB);
                }
            }
            graph.findOrAddFace(vertIds);
        }
        if (this.edgeIndexFns != null) {
            for (int e2 = 0; e2 < this.edgeIndexFns.length; ++e2) {
                if (this.edgeIndexFns[e2].length < 2) {
                    System.out.println("** Add.eval(): Edge index pair does not have two entries.");
                }
                else {
                    final int aid = this.edgeIndexFns[e2][0].eval();
                    final int bid = this.edgeIndexFns[e2][1].eval();
                    if (aid >= graph.vertices().size() || bid >= graph.vertices().size()) {
                        System.out.println("** Add.eval(): Invalid edge vertex index in " + aid + " or " + bid + ".");
                    }
                    else {
                        graph.findOrAddEdge(aid, bid);
                    }
                }
            }
        }
        if (this.faceIndexFns != null) {
            for (int f2 = 0; f2 < this.faceIndexFns.length; ++f2) {
                if (this.faceIndexFns[f2].length < 3) {
                    System.out.println("** Add.eval(): Face index list must have at least three entries.");
                }
                else {
                    final int[] vertIds2 = new int[this.faceIndexFns[f2].length];
                    int n2;
                    for (n2 = 0; n2 < this.faceIndexFns[f2].length; ++n2) {
                        vertIds2[n2] = this.faceIndexFns[f2][n2].eval();
                        if (vertIds2[n2] >= graph.vertices().size()) {
                            System.out.println("** Add.eval(): Invalid face vertex index " + vertIds2[n2] + ".");
                            break;
                        }
                    }
                    if (n2 >= this.faceIndexFns[f2].length) {
                        graph.findOrAddFace(vertIds2);
                    }
                }
            }
        }
        if (this.connect) {
            final double threshold = 1.1 * graph.averageEdgeLength();
            for (final GraphElement vertexA3 : newVertices) {
                for (final GraphElement vertexB3 : graph.elements(SiteType.Vertex)) {
                    if (vertexA3.id() == vertexB3.id()) {
                        continue;
                    }
                    if (MathRoutines.distance(vertexA3.pt2D(), vertexB3.pt2D()) >= threshold) {
                        continue;
                    }
                    graph.findOrAddEdge(vertexA3.id(), vertexB3.id());
                }
            }
        }
        graph.resetBasis();
        graph.resetShape();
        return graph;
    }
    
    @Override
    public boolean isStatic() {
        boolean isStatic = true;
        for (int v = 0; v < this.vertexFns.length; ++v) {
            for (int n = 0; n < this.vertexFns[v].length; ++n) {
                isStatic = (isStatic && this.vertexFns[v][n].isStatic());
            }
        }
        for (int e = 0; e < this.edgeFns.length; ++e) {
            for (int v2 = 0; v2 < this.edgeFns[e].length; ++v2) {
                for (int n2 = 0; n2 < this.edgeFns[e][v2].length; ++n2) {
                    isStatic = (isStatic && this.edgeFns[e][v2][n2].isStatic());
                }
            }
        }
        for (int f = 0; f < this.faceFns.length; ++f) {
            for (int v2 = 0; v2 < this.faceFns[f].length; ++v2) {
                for (int n2 = 0; n2 < this.faceFns[f][v2].length; ++n2) {
                    isStatic = (isStatic && this.faceFns[f][v2][n2].isStatic());
                }
            }
        }
        return isStatic;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        for (int v = 0; v < this.vertexFns.length; ++v) {
            for (int n = 0; n < this.vertexFns[v].length; ++n) {
                flags |= this.vertexFns[v][n].gameFlags(game);
            }
        }
        for (int e = 0; e < this.edgeFns.length; ++e) {
            for (int v2 = 0; v2 < this.edgeFns[e].length; ++v2) {
                for (int n2 = 0; n2 < this.edgeFns[e][v2].length; ++n2) {
                    flags |= this.edgeFns[e][v2][n2].gameFlags(game);
                }
            }
        }
        for (int f = 0; f < this.faceFns.length; ++f) {
            for (int v2 = 0; v2 < this.faceFns[f].length; ++v2) {
                for (int n2 = 0; n2 < this.faceFns[f][v2].length; ++n2) {
                    flags |= this.faceFns[f][v2][n2].gameFlags(game);
                }
            }
        }
        if (game.board().defaultSite() != SiteType.Cell) {
            flags |= 0x800000L;
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        for (int v = 0; v < this.vertexFns.length; ++v) {
            for (int n = 0; n < this.vertexFns[v].length; ++n) {
                this.vertexFns[v][n].preprocess(game);
            }
        }
        for (int e = 0; e < this.edgeFns.length; ++e) {
            for (int v2 = 0; v2 < this.edgeFns[e].length; ++v2) {
                for (int n2 = 0; n2 < this.edgeFns[e][v2].length; ++n2) {
                    this.edgeFns[e][v2][n2].preprocess(game);
                }
            }
        }
        for (int f = 0; f < this.faceFns.length; ++f) {
            for (int v2 = 0; v2 < this.faceFns[f].length; ++v2) {
                for (int n2 = 0; n2 < this.faceFns[f][v2].length; ++n2) {
                    this.faceFns[f][v2][n2].preprocess(game);
                }
            }
        }
        if (this.isStatic()) {
            this.precomputedGraph = this.eval(new Context(game, null), (game.board().defaultSite() == SiteType.Vertex) ? SiteType.Vertex : SiteType.Cell);
        }
    }
}
