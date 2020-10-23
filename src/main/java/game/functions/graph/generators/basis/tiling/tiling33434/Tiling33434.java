// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling33434;

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
public class Tiling33434 extends Basis
{
    private static final long serialVersionUID = 1L;
    private static final double u2 = 0.5;
    private static final double u3;
    public static final double[][] ref;
    
    public Tiling33434(final DimFunction dim) {
        this.basis = BasisType.T33434;
        this.shape = ShapeType.Diamond;
        this.dim = new int[] { dim.eval() };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int rows = this.dim[0];
        final int cols = this.dim[0];
        final List<double[]> vertexList = new ArrayList<>();
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                final Point2D ptRef = xy(r, c);
                for (int n = 0; n < Tiling33434.ref.length; ++n) {
                    final double x = ptRef.getX() + Tiling33434.ref[n][0];
                    final double y = ptRef.getY() + Tiling33434.ref[n][1];
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
    
    public static Point2D.Double xy(final int row, final int col) {
        final double hy;
        final double hx = hy = 1.0 * (1.0 + Math.sqrt(3.0)) / 2.0;
        return new Point2D.Double(hx * (col - row), hy * (row + col));
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        u3 = 1.0 * Math.sqrt(3.0) / 2.0;
        ref = new double[][] { { -0.5 + 0.0 * Tiling33434.u3, -0.5 - 1.0 * Tiling33434.u3 }, { 0.5 + 0.0 * Tiling33434.u3, -0.5 - 1.0 * Tiling33434.u3 }, { -0.5 - 1.0 * Tiling33434.u3, 0.0 - 1.0 * Tiling33434.u3 }, { 0.5 + 1.0 * Tiling33434.u3, 0.0 - 1.0 * Tiling33434.u3 }, { 0.0 + 0.0 * Tiling33434.u3, -0.5 + 0.0 * Tiling33434.u3 }, { -1.0 - 1.0 * Tiling33434.u3, 0.0 + 0.0 * Tiling33434.u3 }, { 0.0 - 1.0 * Tiling33434.u3, 0.0 + 0.0 * Tiling33434.u3 }, { 0.0 + 1.0 * Tiling33434.u3, 0.0 + 0.0 * Tiling33434.u3 }, { 1.0 + 1.0 * Tiling33434.u3, 0.0 + 0.0 * Tiling33434.u3 }, { 0.0 + 0.0 * Tiling33434.u3, 0.5 + 0.0 * Tiling33434.u3 }, { -0.5 - 1.0 * Tiling33434.u3, 0.0 + 1.0 * Tiling33434.u3 }, { 0.5 + 1.0 * Tiling33434.u3, 0.0 + 1.0 * Tiling33434.u3 }, { -0.5 + 0.0 * Tiling33434.u3, 0.5 + 1.0 * Tiling33434.u3 }, { 0.5 + 0.0 * Tiling33434.u3, 0.5 + 1.0 * Tiling33434.u3 } };
    }
}
