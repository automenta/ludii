// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.shape;

import annotations.Name;
import annotations.Or;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.BaseGraphFunction;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Poly;
import game.util.graph.Vertex;
import math.Polygon;
import util.Context;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Repeat extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    private final int rows;
    private final int columns;
    private final Point2D stepColumn;
    private final Point2D stepRow;
    private final List<Polygon> polygons;
    
    public Repeat(final DimFunction rows, final DimFunction columns, @Name final Float[][] step, @Or final Poly poly, @Or final Poly[] polys) {
        this.polygons = new ArrayList<>();
        this.basis = BasisType.NoBasis;
        this.shape = ShapeType.NoShape;
        this.rows = rows.eval();
        this.columns = columns.eval();
        if (step.length < 2 || step[0].length < 2 || step[1].length < 2) {
            System.out.println("** Repeat: Step should contain two pairs of values.");
            this.stepColumn = new Point2D.Double(1.0, 0.0);
            this.stepRow = new Point2D.Double(0.0, 1.0);
        }
        else {
            this.stepColumn = new Point2D.Double(step[0][0], step[0][1]);
            this.stepRow = new Point2D.Double(step[1][0], step[1][1]);
        }
        if (poly != null) {
            this.polygons.add(poly.polygon());
        }
        else {
            for (final Poly ply : polys) {
                this.polygons.add(ply.polygon());
            }
        }
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final double tolerance = 0.01;
        final Graph graph = new Graph();
        for (int row = 0; row < this.rows; ++row) {
            for (int col = 0; col < this.columns; ++col) {
                final Point2D ptRef = new Point2D.Double(col * this.stepColumn.getX() + row * this.stepRow.getX(), col * this.stepColumn.getY() + row * this.stepRow.getY());
                for (final Polygon polygon : this.polygons) {
                    for (int n = 0; n < polygon.points().size(); ++n) {
                        final Point2D ptA = polygon.points().get(n);
                        final Point2D ptB = polygon.points().get((n + 1) % polygon.points().size());
                        final Vertex vertexA = graph.findOrAddVertex(ptRef.getX() + ptA.getX(), ptRef.getY() + ptA.getY(), 0.01);
                        final Vertex vertexB = graph.findOrAddVertex(ptRef.getX() + ptB.getX(), ptRef.getY() + ptB.getY(), 0.01);
                        graph.findOrAddEdge(vertexA, vertexB);
                    }
                }
            }
        }
        graph.makeFaces(false);
        graph.reorder();
        return graph;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
