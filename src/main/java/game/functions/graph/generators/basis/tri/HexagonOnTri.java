// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tri;

import annotations.Hide;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import util.Context;

@Hide
public class HexagonOnTri extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public HexagonOnTri(final DimFunction dim) {
        this.basis = BasisType.Triangular;
        this.shape = ShapeType.Hexagon;
        this.dim = new int[] { dim.eval() };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int d = this.dim[0] + ((siteType == SiteType.Cell) ? 1 : 0);
        final int rows = 2 * d - 1;
        final int cols = 2 * d - 1;
        final Graph graph = new Graph();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (col <= cols / 2 + row) {
                    if (row - col <= cols / 2) {
                        graph.addVertex(Tri.xy(row, col));
                    }
                }
            }
        }
        int vid = 0;
        for (int n = 0; n <= rows / 2; ++n) {
            for (int col2 = 0; col2 < cols / 2 + n; ++col2) {
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + 1));
                ++vid;
            }
            ++vid;
        }
        for (int n = 0; n < rows / 2; ++n) {
            for (int col2 = 0; col2 < cols - 2 - n; ++col2) {
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + 1));
                ++vid;
            }
            ++vid;
        }
        vid = 0;
        for (int n = 0; n < rows / 2; ++n) {
            final int off = rows / 2 + 1 + n;
            for (int col3 = 0; col3 < cols / 2 + 1 + n; ++col3) {
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + off));
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid + off + 1));
                ++vid;
            }
        }
        vid += cols;
        for (int n = 0; n < rows / 2; ++n) {
            final int off = rows - n;
            for (int col3 = 0; col3 < cols - 1 - n; ++col3) {
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid - off));
                graph.addEdge(graph.vertices().get(vid), graph.vertices().get(vid - off + 1));
                ++vid;
            }
        }
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
