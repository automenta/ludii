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
public class StarOnTri extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public StarOnTri(final DimFunction dim) {
        this.basis = BasisType.Triangular;
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
                else if (r <= 2 * d) {
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
                final Point2D pt = Tri.xy(r, c);
                vertexList.add(new double[] { pt.getX(), pt.getY() });
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
