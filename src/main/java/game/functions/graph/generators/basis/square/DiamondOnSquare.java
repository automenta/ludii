// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.square;

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

import java.util.ArrayList;
import java.util.List;

@Hide
public class DiamondOnSquare extends Basis
{
    private static final long serialVersionUID = 1L;
    private final DiagonalsType diagonals;
    
    public DiamondOnSquare(final DimFunction dim, final DiagonalsType diagonals) {
        this.basis = BasisType.Square;
        this.shape = ShapeType.Diamond;
        this.dim = new int[] { dim.eval() };
        this.diagonals = diagonals;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int d = this.dim[0];
        final int rows = 2 * d;
        final int cols = 2 * d;
        final List<double[]> vertexList = new ArrayList<>();
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                if (r + c >= d - 1 && c - r <= d && r - c <= d) {
                    if (r + c < 3 * d) {
                        vertexList.add(new double[] { c, r });
                    }
                }
            }
        }
        final Graph graph = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        Square.handleDiagonals(graph, 0, rows, 0, cols, this.diagonals, 1.0E-4);
        graph.makeFaces(false);
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
