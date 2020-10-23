// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling3636;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import math.MathRoutines;
import util.Context;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class Tiling3636 extends Basis
{
    private static final long serialVersionUID = 1L;
    public static final double ux;
    public static final double uy = 1.0;
    public static final double[][] ref;
    
    public Tiling3636(final DimFunction dimA, @Opt final DimFunction dimB) {
        this.basis = BasisType.T3636;
        if (dimB == null) {
            this.shape = ShapeType.Hexagon;
            this.dim = new int[] { dimA.eval() };
        }
        else {
            this.shape = ShapeType.Rhombus;
            this.dim = new int[] { dimA.eval(), dimB.eval() };
        }
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int rows = 2 * this.dim[0] - 1;
        final int cols = 2 * ((this.dim.length < 2) ? this.dim[0] : this.dim[1]) - 1;
        final List<double[]> vertexList = new ArrayList<>();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (this.shape == ShapeType.Hexagon) {
                    if (col > cols / 2 + row) {
                        continue;
                    }
                    if (row - col > cols / 2) {
                        continue;
                    }
                }
                if (this.shape != ShapeType.Triangle || row <= col) {
                    final Point2D ptRef = xy(row, col);
                    for (int n = 0; n < Tiling3636.ref.length; ++n) {
                        final double x = ptRef.getX() + Tiling3636.ref[n][1];
                        final double y = ptRef.getY() + Tiling3636.ref[n][0];
                        int vid;
                        for (vid = 0; vid < vertexList.size(); ++vid) {
                            final double[] ptV = vertexList.get(vid);
                            final double dist = MathRoutines.distance(ptV[0], ptV[1], x, y);
                            if (dist < 0.1) {
                                break;
                            }
                        }
                        if (vid >= vertexList.size()) {
                            vertexList.add(new double[] { x, y });
                        }
                    }
                }
            }
        }
        final Graph graph = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        graph.reorder();
        return graph;
    }
    
    public static Point2D xy(final int row, final int col) {
        final double hx = 2.0;
        final double hy = Math.sqrt(3.0);
        return new Point2D.Double(2.0 * (col - 0.5 * row), hy * row);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        ux = 1.0 * Math.sqrt(3.0) / 2.0;
        ref = new double[][] { { 0.0 * Tiling3636.ux, 1.0 }, { 1.0 * Tiling3636.ux, 0.5 }, { 1.0 * Tiling3636.ux, -0.5 }, { 0.0 * Tiling3636.ux, -1.0 }, { -1.0 * Tiling3636.ux, -0.5 }, { -1.0 * Tiling3636.ux, 0.5 } };
    }
}
