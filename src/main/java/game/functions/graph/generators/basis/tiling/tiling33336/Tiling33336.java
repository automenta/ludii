// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling33336;

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
public class Tiling33336 extends Basis
{
    private static final long serialVersionUID = 1L;
    public static final double ux = 0.5;
    public static final double uy;
    public static final double[][] ref;
    
    public Tiling33336(final DimFunction dim) {
        this.basis = BasisType.T33336;
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
                    for (int n = 0; n < Tiling33336.ref.length; ++n) {
                        final double x = ptRef.getX() + Tiling33336.ref[n][0];
                        final double y = ptRef.getY() + Tiling33336.ref[n][1];
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
        return new Point2D.Double(col * 5 * 0.5 - row * 4 * 0.5, row * 2 * Tiling33336.uy + col * Tiling33336.uy);
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
        ref = new double[][] { { -0.5, 1.0 * Tiling33336.uy }, { 0.5, 1.0 * Tiling33336.uy }, { 1.0, 0.0 * Tiling33336.uy }, { 0.5, -1.0 * Tiling33336.uy }, { -0.5, -1.0 * Tiling33336.uy }, { -1.0, 0.0 * Tiling33336.uy }, { -1.0, 2.0 * Tiling33336.uy }, { 0.0, 2.0 * Tiling33336.uy }, { 1.0, 2.0 * Tiling33336.uy }, { 1.5, 1.0 * Tiling33336.uy }, { 2.0, 0.0 * Tiling33336.uy }, { 1.5, -1.0 * Tiling33336.uy }, { 1.0, -2.0 * Tiling33336.uy }, { 0.0, -2.0 * Tiling33336.uy }, { -1.0, -2.0 * Tiling33336.uy }, { -1.5, -1.0 * Tiling33336.uy }, { -2.0, 0.0 * Tiling33336.uy }, { -1.5, 1.0 * Tiling33336.uy } };
    }
}
