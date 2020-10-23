// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.square;

import annotations.Hide;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import util.Context;

import java.awt.geom.Point2D;

@Hide
public class RectangleOnSquare extends Basis
{
    private static final long serialVersionUID = 1L;
    private final DiagonalsType diagonals;
    private final boolean pyramidal;
    
    public RectangleOnSquare(final DimFunction rows, final DimFunction columns, final DiagonalsType diagonals, final Boolean pyramidal) {
        this.basis = BasisType.Square;
        this.shape = ShapeType.Rectangle;
        this.diagonals = diagonals;
        this.pyramidal = (pyramidal != null && pyramidal);
        this.dim = new int[] { rows.eval(), (columns == null) ? rows.eval() : columns.eval() };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final double tolerance = 0.01;
        final Graph graph = new Graph();
        final int rows = this.dim[0] + ((siteType == SiteType.Cell) ? 1 : 0);
        final int cols = this.dim[1] + ((siteType == SiteType.Cell) ? 1 : 0);
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                final Point2D pt = new Point2D.Double(col, row);
                graph.addVertex(pt);
            }
        }
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                final Vertex vertexA = graph.findVertex(col, row, 0.01);
                for (int dirn = 0; dirn < Square.steps.length / 2; ++dirn) {
                    final int rr = row + Square.steps[dirn][0];
                    final int cc = col + Square.steps[dirn][1];
                    if (rr >= 0 && rr < rows && cc >= 0) {
                        if (cc < cols) {
                            final Vertex vertexB = graph.findVertex(cc, rr, 0.01);
                            if (vertexA != null && vertexB != null) {
                                graph.findOrAddEdge(vertexA, vertexB);
                            }
                        }
                    }
                }
            }
        }
        Square.handleDiagonals(graph, 0, rows, 0, cols, this.diagonals, 0.01);
        if (this.pyramidal) {
            final double dz = 1.0 / Math.sqrt(2.0);
            for (int layers = rows, layer = 1; layer < layers; ++layer) {
                final double offX = layer * 0.5;
                final double offY = layer * 0.5;
                final double offZ = layer * dz;
                for (int row2 = 0; row2 < rows - layer; ++row2) {
                    for (int col2 = 0; col2 < cols - layer; ++col2) {
                        graph.findOrAddVertex(offX + col2, offY + row2, offZ, 0.01);
                    }
                }
            }
            graph.makeEdges();
        }
        graph.makeFaces(false);
        graph.setBasisAndShape(this.basis, this.shape);
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
