// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.shape;

import annotations.Opt;
import game.Game;
import game.functions.dim.DimFunction;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import math.MathRoutines;
import util.Context;

public class Wedge extends Shape
{
    private static final long serialVersionUID = 1L;
    
    public Wedge(final DimFunction rows, @Opt final DimFunction columns) {
        super(BasisType.NoBasis, ShapeType.Wedge, rows, columns);
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int rows = this.dim[0];
        final int columns = (this.dim.length == 1) ? 3 : this.dim[1];
        final Graph graph = new Graph();
        Vertex pivot = null;
        final int mid = rows - 1;
        for (int r = 0; r < rows; ++r) {
            if (r == 0) {
                pivot = graph.addVertex(mid, mid);
            }
            else {
                final int left = mid - r;
                final int right = mid + r;
                for (int c = 0; c < columns; ++c) {
                    final double t = c / (double)(columns - 1);
                    final double x = MathRoutines.lerp(t, left, right);
                    final double y = mid - r;
                    graph.addVertex(x, y);
                }
            }
        }
        for (int r = 0; r < rows; ++r) {
            if (r == 0) {
                for (int c2 = 0; c2 < columns; ++c2) {
                    graph.addEdge(0, c2 + 1);
                }
            }
            else {
                final int from = r * columns - columns + 1;
                for (int c3 = 0; c3 < columns - 1; ++c3) {
                    graph.addEdge(from + c3, from + c3 + 1);
                }
                if (r < rows - 1) {
                    for (int c3 = 0; c3 < columns; ++c3) {
                        graph.addEdge(from + c3, from + columns + c3);
                    }
                }
            }
        }
        for (final Vertex vertex : graph.vertices()) {
            vertex.setPivot(pivot);
        }
        graph.setBasisAndShape(this.basis, this.shape);
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
