// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling3464;

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
public class DiamondOn3464 extends Basis
{
    private static final long serialVersionUID = 1L;
    private static final double ux = 1.0;
    private static final double uy;
    private static final double[][] ref;
    
    public DiamondOn3464(final DimFunction dimA, @Opt final DimFunction dimB) {
        final double a = 1.0 + Math.sqrt(3.0) / 2.0;
        final double h = a / Math.cos(Math.toRadians(15.0));
        for (int n = 0; n < 12; ++n) {
            final double theta = Math.toRadians(15 + n * 30);
            DiamondOn3464.ref[6 + n][0] = h * Math.cos(theta);
            DiamondOn3464.ref[6 + n][1] = h * Math.sin(theta);
        }
        this.basis = BasisType.T3464;
        this.shape = ((dimB == null) ? ShapeType.Diamond : ShapeType.Prism);
        this.dim = ((dimB == null) ? new int[] { dimA.eval() } : new int[] { dimA.eval(), dimB.eval() });
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final boolean isPrism = this.shape == ShapeType.Prism;
        final int rows = this.dim[0];
        final int cols = isPrism ? this.dim[1] : this.dim[0];
        final List<double[]> vertexList = new ArrayList<>();
        if (isPrism) {
            for (int r = 0; r < rows + cols - 1; ++r) {
                for (int c = 0; c < cols + rows - 1; ++c) {
                    if (Math.abs(r - c) < rows) {
                        addVertex(r, c, vertexList);
                    }
                }
            }
        }
        else {
            for (int r = 0; r < rows; ++r) {
                for (int c = 0; c < cols; ++c) {
                    addVertex(r, c, vertexList);
                }
            }
        }
        final Graph result = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        result.reorder();
        return result;
    }
    
    static void addVertex(final int row, final int col, final List<double[]> vertexList) {
        final Point2D ptRef = xy(row, col);
        for (int n = 0; n < DiamondOn3464.ref.length; ++n) {
            final double x = ptRef.getX() + DiamondOn3464.ref[n][0];
            final double y = ptRef.getY() + DiamondOn3464.ref[n][1];
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
    
    static Point2D xy(final int row, final int col) {
        final double hx = 1.0 * (1.0 + Math.sqrt(3.0));
        final double hy = 1.0 * (3.0 + Math.sqrt(3.0)) / 2.0;
        return new Point2D.Double(hy * (col - row), hx * (row + col) * 0.5);
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
        ref = new double[][] { { -0.5, 1.0 * DiamondOn3464.uy }, { 0.5, 1.0 * DiamondOn3464.uy }, { 1.0, 0.0 * DiamondOn3464.uy }, { 0.5, -1.0 * DiamondOn3464.uy }, { -0.5, -1.0 * DiamondOn3464.uy }, { -1.0, 0.0 * DiamondOn3464.uy }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 } };
    }
}
