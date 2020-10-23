// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.hex;

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
public class DiamondOnHex extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public DiamondOnHex(final DimFunction dimA, @Opt final DimFunction dimB) {
        this.basis = BasisType.Hexagonal;
        this.shape = ((dimB == null) ? ShapeType.Diamond : ShapeType.Prism);
        this.dim = ((dimB == null) ? new int[] { dimA.eval() } : new int[] { dimA.eval(), dimB.eval() });
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final boolean isPrism = this.shape == ShapeType.Prism;
        final int rows = this.dim[0];
        final int cols = isPrism ? this.dim[1] : this.dim[0];
        final List<double[]> vertexList = new ArrayList<>();
        final int maxRows = isPrism ? (rows + cols - 1) : rows;
        final int maxCols = isPrism ? (rows + cols - 1) : cols;
        for (int row = 0; row < maxRows; ++row) {
            for (int col = 0; col < maxCols; ++col) {
                if (!isPrism || Math.abs(row - col) < rows) {
                    final Point2D ptRef = xy(row, col);
                    for (int n = 0; n < Hex.ref.length; ++n) {
                        final double x = ptRef.getX() + Hex.ref[n][1];
                        final double y = ptRef.getY() + Hex.ref[n][0];
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
    
    static Point2D xy(final int row, final int col) {
        final double hx = 1.0 * Math.sqrt(3.0);
        final double hy = 1.5;
        return new Point2D.Double(1.5 * (col - row), hx * (row + col) * 0.5);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
