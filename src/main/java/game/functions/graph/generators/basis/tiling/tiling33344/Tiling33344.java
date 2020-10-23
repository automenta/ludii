// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling33344;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.GraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Poly;
import math.MathRoutines;
import util.Context;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class Tiling33344 extends Basis
{
    private static final long serialVersionUID = 1L;
    public static final double[][] ref;
    
    public Tiling33344(final DimFunction dimA, @Opt final DimFunction dimB) {
        this.basis = BasisType.T33344;
        this.shape = ShapeType.Rhombus;
        this.dim = new int[] { dimA.eval(), (dimB == null) ? dimA.eval() : dimB.eval() };
    }
    
    public static GraphFunction construct(@Or final Poly poly, @Or final DimFunction[] sides) {
        int numNonNull = 0;
        if (poly != null) {
            ++numNonNull;
        }
        if (sides != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Exactly one array parameter must be non-null.");
        }
        if (poly != null) {
            return new CustomOn33344(poly.polygon());
        }
        return new CustomOn33344(sides);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int rows = this.dim[0];
        final int cols = this.dim[1];
        final List<double[]> vertexList = new ArrayList<>();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                final Point2D ptRef = xy(row, col);
                for (int n = 0; n < Tiling33344.ref.length; ++n) {
                    final double x = ptRef.getX() + Tiling33344.ref[n][1];
                    final double y = ptRef.getY() + Tiling33344.ref[n][0];
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
    
    public static Point2D xy(final int row, final int col) {
        final double dx = 1.0;
        final double dy = 1.0 * (1.0 + Math.sqrt(3.0) / 2.0);
        return new Point2D.Double((col + 0.5 * row) * 1.0, row * dy);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        ref = new double[][] { { 0.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 }, { 1.0, 0.0 } };
    }
}
