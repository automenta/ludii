// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.quadhex;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Edge;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import math.MathRoutines;
import util.Context;

import java.awt.geom.Point2D;

public class Quadhex extends Basis
{
    private static final long serialVersionUID = 1L;
    private final boolean thirds;
    
    public Quadhex(final DimFunction layers, @Opt @Name final Boolean thirds) {
        this.basis = BasisType.QuadHex;
        this.shape = ShapeType.Hexagon;
        this.dim = new int[] { layers.eval() };
        this.thirds = (thirds != null && thirds);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final Graph graph = new Graph();
        final int layers = this.dim[0];
        if (this.thirds) {
            this.threeUniformSections(graph, layers);
        }
        else {
            this.sixUniformSections(graph, layers);
        }
        graph.makeFaces(true);
        graph.setBasisAndShape(this.basis, this.shape);
        graph.reorder();
        return graph;
    }
    
    void sixUniformSections(final Graph graph, final int layers) {
        final double tolerance = 0.001;
        final Point2D ptA = new Point2D.Double(0.0, 0.0);
        final Point2D ptB = new Point2D.Double(0.0, layers * Math.sqrt(3.0) / 2.0);
        final Point2D ptC = new Point2D.Double(layers / 2.0, layers * Math.sqrt(3.0) / 2.0);
        final Point2D ptE = new Point2D.Double(layers, 0.0);
        final Point2D ptD = new Point2D.Double((ptC.getX() + ptE.getX()) / 2.0, (ptC.getY() + ptE.getY()) / 2.0);
        for (int rotn = 0; rotn < 6; ++rotn) {
            final double theta = rotn * 3.141592653589793 / 3.0;
            for (int row = 0; row < layers; ++row) {
                final double r0 = row / (double)layers;
                final double r2 = (row + 1) / (double)layers;
                final Point2D ptAD0 = MathRoutines.rotate(theta, MathRoutines.lerp(r0, ptA, ptD));
                final Point2D ptAD2 = MathRoutines.rotate(theta, MathRoutines.lerp(r2, ptA, ptD));
                final Point2D ptBC0 = MathRoutines.rotate(theta, MathRoutines.lerp(r0, ptB, ptC));
                final Point2D ptBC2 = MathRoutines.rotate(theta, MathRoutines.lerp(r2, ptB, ptC));
                for (int col = 0; col < layers; ++col) {
                    final double c0 = col / (double)layers;
                    final double c2 = (col + 1) / (double)layers;
                    final Point2D ptAB0 = MathRoutines.lerp(c0, ptAD0, ptBC0);
                    final Point2D ptAB2 = MathRoutines.lerp(c2, ptAD0, ptBC0);
                    final Point2D ptDC1 = MathRoutines.lerp(c2, ptAD2, ptBC2);
                    final Vertex vertexA = graph.findOrAddVertex(ptAB0, 0.001);
                    final Vertex vertexB = graph.findOrAddVertex(ptAB2, 0.001);
                    final Vertex vertexC = graph.findOrAddVertex(ptDC1, 0.001);
                    graph.findOrAddEdge(vertexA, vertexB);
                    graph.findOrAddEdge(vertexB, vertexC);
                    if (row == layers - 1) {
                        final Point2D ptDC2 = MathRoutines.lerp(c0, ptAD2, ptBC2);
                        final Vertex vertexD = graph.findOrAddVertex(ptDC2, 0.001);
                        graph.findOrAddEdge(vertexC, vertexD);
                    }
                }
            }
        }
    }
    
    void threeUniformSections(final Graph graph, final int layers) {
        final double tolerance = 0.001;
        final double L32 = layers * Math.sqrt(3.0) / 2.0;
        final Point2D ptO = new Point2D.Double(0.0, 0.0);
        final Point2D ptA = new Point2D.Double(-layers / 2.0, -L32);
        final Point2D ptC = new Point2D.Double(-layers, 0.0);
        final Point2D ptE = new Point2D.Double(0.0, -L32);
        final double ratio = layers / (layers + 0.5);
        final Point2D ptF = MathRoutines.lerp(ratio, ptE, ptO);
        final Point2D ptG = MathRoutines.lerp(ratio / 2.0, ptA, ptC);
        final Graph section = new Graph();
        for (int row = 0; row < layers; ++row) {
            final double r0 = row / (double)layers;
            final double r2 = (row + 1) / (double)layers;
            final Point2D ptAE0 = MathRoutines.lerp(r0, ptA, ptE);
            final Point2D ptAE2 = MathRoutines.lerp(r2, ptA, ptE);
            final Point2D ptGF0 = MathRoutines.lerp(r0, ptG, ptF);
            final Point2D ptGF2 = MathRoutines.lerp(r2, ptG, ptF);
            for (int col = 0; col < layers; ++col) {
                final double c0 = col / (double)layers;
                final double c2 = (col + 1) / (double)layers;
                final Point2D ptAG0 = MathRoutines.lerp(c0, ptAE0, ptGF0);
                final Point2D ptAG2 = MathRoutines.lerp(c2, ptAE0, ptGF0);
                final Point2D ptEF0 = MathRoutines.lerp(c0, ptAE2, ptGF2);
                final Point2D ptEF2 = MathRoutines.lerp(c2, ptAE2, ptGF2);
                final Vertex vertexA = section.findOrAddVertex(ptAG0, 0.001);
                final Vertex vertexB = section.findOrAddVertex(ptAG2, 0.001);
                final Vertex vertexC = section.findOrAddVertex(ptEF0, 0.001);
                final Vertex vertexD = section.findOrAddVertex(ptEF2, 0.001);
                section.findOrAddEdge(vertexA, vertexB);
                section.findOrAddEdge(vertexC, vertexD);
                section.findOrAddEdge(vertexA, vertexC);
                section.findOrAddEdge(vertexB, vertexD);
                final Point2D ptAA = new Point2D.Double(-ptAG0.getX(), ptAG0.getY());
                final Point2D ptBB = new Point2D.Double(-ptAG2.getX(), ptAG2.getY());
                final Point2D ptCC = new Point2D.Double(-ptEF0.getX(), ptEF0.getY());
                final Point2D ptDD = new Point2D.Double(-ptEF2.getX(), ptEF2.getY());
                final Vertex vertexAA = section.findOrAddVertex(ptAA, 0.001);
                final Vertex vertexBB = section.findOrAddVertex(ptBB, 0.001);
                final Vertex vertexCC = section.findOrAddVertex(ptCC, 0.001);
                final Vertex vertexDD = section.findOrAddVertex(ptDD, 0.001);
                section.findOrAddEdge(vertexAA, vertexBB);
                section.findOrAddEdge(vertexCC, vertexDD);
                section.findOrAddEdge(vertexAA, vertexCC);
                section.findOrAddEdge(vertexBB, vertexDD);
            }
        }
        final int verticesPerSection = section.vertices().size();
        final double theta = 2.0943951023931953;
        final Vertex[][] save = new Vertex[3][3];
        for (int rotn = 0; rotn < 3; ++rotn) {
            for (final Vertex vertex : section.vertices()) {
                graph.addVertex(vertex.pt2D().getX(), vertex.pt2D().getY());
            }
            for (final Edge edge : section.edges()) {
                graph.addEdge(edge.vertexA().id() + rotn * verticesPerSection, edge.vertexB().id() + rotn * verticesPerSection);
            }
            for (final Vertex vertex : section.vertices()) {
                final double dx = vertex.pt().x();
                final double dy = vertex.pt().y();
                final double xx = dx * Math.cos(2.0943951023931953) - dy * Math.sin(2.0943951023931953);
                final double yy = dy * Math.cos(2.0943951023931953) + dx * Math.sin(2.0943951023931953);
                vertex.pt().set(xx, yy);
            }
            save[0][rotn] = graph.vertices().get(rotn * verticesPerSection + 4 * layers);
            save[1][rotn] = graph.vertices().get(rotn * verticesPerSection + 4 * layers + 2);
            save[2][rotn] = graph.vertices().get(rotn * verticesPerSection + layers * (2 * layers + 3));
        }
        graph.findOrAddEdge(save[2][0], save[2][1]);
        graph.findOrAddEdge(save[2][1], save[2][2]);
        graph.findOrAddEdge(save[2][2], save[2][0]);
        graph.findOrAddEdge(save[1][0], save[0][1]);
        graph.findOrAddEdge(save[1][1], save[0][2]);
        graph.findOrAddEdge(save[1][2], save[0][0]);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
