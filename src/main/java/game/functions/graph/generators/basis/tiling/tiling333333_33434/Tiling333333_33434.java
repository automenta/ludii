// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling333333_33434;

import annotations.Hide;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import main.math.MathRoutines;
import util.Context;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class Tiling333333_33434 extends Basis
{
    private static final long serialVersionUID = 1L;
    public static final double ux = 1.0;
    public static final double uy;
    public static final double[][] ref;
    
    public Tiling333333_33434(final DimFunction dim) {
        final double a = 1.0 + Math.sqrt(3.0) / 2.0;
        final double h = a / Math.cos(Math.toRadians(15.0));
        for (int n = 0; n < 12; ++n) {
            final double theta = Math.toRadians(15 + n * 30);
            Tiling333333_33434.ref[6 + n][0] = h * Math.cos(theta);
            Tiling333333_33434.ref[6 + n][1] = h * Math.sin(theta);
        }
        this.basis = BasisType.T333333_33434;
        this.shape = ShapeType.Hexagon;
        this.dim = new int[] { dim.eval() };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int rows = this.dim[0] * 2 - 1;
        final int cols = this.dim[0] * 2 - 1;
        final List<double[]> vertexList = new ArrayList<>();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (Math.abs(row - col) <= rows / 2) {
                    final Point2D ptRef = xy(row, col);
                    for (int n = 0; n < Tiling333333_33434.ref.length; ++n) {
                        final double x = ptRef.getX() + Tiling333333_33434.ref[n][0];
                        final double y = ptRef.getY() + Tiling333333_33434.ref[n][1];
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
    
    public static Point2D.Double xy(final int row, final int col) {
        final double hx = 1.0 * (1.5 + Math.sqrt(3.0));
        final double hy = 1.0 * (2.0 + Math.sqrt(3.0));
        return new Point2D.Double(hx * (col - row), hy * (row + col) * 0.5);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        uy = 1.0 * Math.sqrt(3.0) / 2.0;
        ref = new double[][] { { -0.5, 1.0 * Tiling333333_33434.uy }, { 0.5, 1.0 * Tiling333333_33434.uy }, { 1.0, 0.0 * Tiling333333_33434.uy }, { 0.5, -1.0 * Tiling333333_33434.uy }, { -0.5, -1.0 * Tiling333333_33434.uy }, { -1.0, 0.0 * Tiling333333_33434.uy }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 } };
    }
}
