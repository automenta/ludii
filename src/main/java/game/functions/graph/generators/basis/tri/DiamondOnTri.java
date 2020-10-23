// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tri;

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
import util.Context;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class DiamondOnTri extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public DiamondOnTri(final DimFunction dimA, @Opt final DimFunction dimB) {
        this.basis = BasisType.Triangular;
        this.shape = ((dimB == null) ? ShapeType.Diamond : ShapeType.Prism);
        this.dim = ((dimB == null) ? new int[] { dimA.eval() } : new int[] { dimA.eval(), dimB.eval() });
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final boolean isPrism = this.shape == ShapeType.Prism;
        final int rows = this.dim[0] + ((siteType == SiteType.Cell) ? 1 : 0);
        final int cols = (isPrism ? this.dim[1] : this.dim[0]) + ((siteType == SiteType.Cell) ? 1 : 0);
        final List<double[]> vertexList = new ArrayList<>();
        if (isPrism) {
            for (int r = 0; r < rows + cols - 1; ++r) {
                for (int c = 0; c < cols + rows - 1; ++c) {
                    if (Math.abs(r - c) < rows) {
                        final Point2D pt = xy(r, c);
                        vertexList.add(new double[] { pt.getX(), pt.getY() });
                    }
                }
            }
        }
        else {
            for (int r = 0; r < rows; ++r) {
                for (int c = 0; c < cols; ++c) {
                    final Point2D pt = xy(r, c);
                    vertexList.add(new double[] { pt.getX(), pt.getY() });
                }
            }
        }
        final Graph graph = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        graph.reorder();
        return graph;
    }
    
    static Point2D xy(final int row, final int col) {
        final double hx = 1.0;
        final double hy = Math.sqrt(3.0) / 2.0;
        return new Point2D.Double(hy * (col - row), 1.0 * (row + col) * 0.5);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
