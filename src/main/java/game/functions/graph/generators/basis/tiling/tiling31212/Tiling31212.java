// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling31212;

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
public class Tiling31212 extends Basis
{
    private static final long serialVersionUID = 1L;
    public static final double ux = 0.5;
    public static final double uy;
    public static final double[][] ref;
    
    public Tiling31212(final DimFunction dim) {
        final double r = 1.0 / Math.sqrt(2.0 - Math.sqrt(3.0));
        final double off = 0.2617993877991494;
        for (int s = 0; s < 12; ++s) {
            final double t = s / 12.0;
            final double theta = 0.2617993877991494 + t * 2.0 * 3.141592653589793;
            Tiling31212.ref[s][0] = r * Math.cos(theta);
            Tiling31212.ref[s][1] = r * Math.sin(theta);
        }
        this.basis = BasisType.T31212;
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
                    for (int n = 0; n < Tiling31212.ref.length; ++n) {
                        final double x = ptRef.getX() + Tiling31212.ref[n][0];
                        final double y = ptRef.getY() + Tiling31212.ref[n][1];
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
        final double dx = 3.7320508;
        final double dy = 3.7320508 * Math.sqrt(3.0) / 2.0;
        return new Point2D.Double((col - 0.5 * row) * 3.7320508, row * dy);
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
        ref = new double[12][2];
    }
}
