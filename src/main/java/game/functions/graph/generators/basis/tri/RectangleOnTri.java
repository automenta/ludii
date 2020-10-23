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
public class RectangleOnTri extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public RectangleOnTri(final DimFunction dimA, @Opt final DimFunction dimB) {
        final int rows = dimA.eval();
        final int cols = (dimB != null) ? dimB.eval() : rows;
        this.basis = BasisType.Triangular;
        this.shape = ((rows == cols) ? ShapeType.Square : ShapeType.Rectangle);
        this.dim = new int[] { rows, cols };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int rows = this.dim[0] + ((siteType == SiteType.Cell) ? 1 : 0);
        final int cols = this.dim[1] + ((siteType == SiteType.Cell) ? 1 : 0);
        final List<double[]> vertexList = new ArrayList<>();
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols + rows; ++c) {
                if (c >= (r + 1) / 2) {
                    if (c < cols + r / 2) {
                        final Point2D pt = Tri.xy(r, c);
                        vertexList.add(new double[] { pt.getX(), pt.getY() });
                    }
                }
            }
        }
        final Graph graph = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        graph.reorder();
        return graph;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
