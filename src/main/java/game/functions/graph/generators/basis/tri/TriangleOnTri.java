// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tri;

import annotations.Hide;
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
public class TriangleOnTri extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public TriangleOnTri(final DimFunction dim) {
        this.basis = BasisType.Triangular;
        this.shape = ShapeType.Triangle;
        this.dim = new int[] { dim.eval() };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int rows = this.dim[0] + ((siteType == SiteType.Cell) ? 1 : 0);
        final int cols = this.dim[0] + ((siteType == SiteType.Cell) ? 1 : 0);
        final List<double[]> vertexList = new ArrayList<>();
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                if (r <= c) {
                    final Point2D pt = Tri.xy(r, c);
                    vertexList.add(new double[] { pt.getX(), pt.getY() });
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
