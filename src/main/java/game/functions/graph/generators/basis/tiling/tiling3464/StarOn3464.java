// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling3464;

import annotations.Hide;
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
public class StarOn3464 extends Basis
{
    private static final long serialVersionUID = 1L;
    private static final double[][] ref;
    
    public StarOn3464(final DimFunction dim) {
        final double a = 1.0 + Math.sqrt(3.0) / 2.0;
        final double h = a / Math.cos(Math.toRadians(15.0));
        for (int n = 0; n < 12; ++n) {
            final double theta = Math.toRadians(15 + n * 30);
            StarOn3464.ref[6 + n][0] = h * Math.cos(theta);
            StarOn3464.ref[6 + n][1] = h * Math.sin(theta);
        }
        this.basis = BasisType.T3464;
        this.shape = ShapeType.Star;
        this.dim = new int[] { dim.eval() };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int d = this.dim[0];
        final int rows = 4 * this.dim[0] + 1;
        final int cols = 4 * this.dim[0] + 1;
        final List<double[]> vertexList = new ArrayList<>();
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                if (r < d) {
                    if (c < d) {
                        continue;
                    }
                    if (c - r > d) {
                        continue;
                    }
                }
                if (r <= 2 * d) {
                    if (r - c > d) {
                        continue;
                    }
                    if (c >= cols - d) {
                        continue;
                    }
                }
                else if (r <= 3 * d) {
                    if (c < d) {
                        continue;
                    }
                    if (c - r > d) {
                        continue;
                    }
                }
                else {
                    if (c > 3 * d) {
                        continue;
                    }
                    if (r - c > d) {
                        continue;
                    }
                }
                final Point2D ptRef = xy(r, c);
                for (int n = 0; n < StarOn3464.ref.length; ++n) {
                    final double x = ptRef.getX() + StarOn3464.ref[n][1];
                    final double y = ptRef.getY() + StarOn3464.ref[n][0];
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
        final Graph graph = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        graph.reorder();
        return graph;
    }
    
    static Point2D xy(final int row, final int col) {
        final double hx = 1.0 * (1.0 + Math.sqrt(3.0));
        final double hy = 1.0 * (3.0 + Math.sqrt(3.0)) / 2.0;
        return new Point2D.Double(hx * (col - 0.5 * row), hy * row);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        ref = new double[][] { { -0.5, 1.0 * Tiling3464.uy }, { 0.5, 1.0 * Tiling3464.uy }, { 1.0, 0.0 * Tiling3464.uy }, { 0.5, -1.0 * Tiling3464.uy }, { -0.5, -1.0 * Tiling3464.uy }, { -1.0, 0.0 * Tiling3464.uy }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 } };
    }
}
