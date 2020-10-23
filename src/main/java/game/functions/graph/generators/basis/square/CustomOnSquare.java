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
import gnu.trove.list.array.TIntArrayList;
import math.Polygon;
import util.Context;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class CustomOnSquare extends Basis
{
    private static final long serialVersionUID = 1L;
    private final Polygon polygon;
    private final TIntArrayList sides;
    private final DiagonalsType diagonals;
    
    public CustomOnSquare(final Polygon polygon, final DiagonalsType diagonals) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        this.basis = BasisType.Square;
        this.shape = ShapeType.Custom;
        this.polygon.setFrom(polygon);
        this.diagonals = diagonals;
    }
    
    public CustomOnSquare(final DimFunction[] sides, final DiagonalsType diagonals) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        this.basis = BasisType.Square;
        this.shape = ShapeType.Custom;
        for (DimFunction side : sides) {
            this.sides.add(side.eval());
        }
        this.diagonals = diagonals;
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.polygon.isEmpty() && !this.sides.isEmpty()) {
            this.polygon.fromSides(this.sides, Square.steps);
        }
        this.polygon.inflate(0.1);
        final Rectangle2D bounds = this.polygon.bounds();
        final int fromCol = (int)bounds.getMinX() - 2;
        final int fromRow = (int)bounds.getMinY() - 2;
        final int toCol = (int)bounds.getMaxX() + 2;
        final int toRow = (int)bounds.getMaxY() + 2;
        final List<double[]> vertexList = new ArrayList<>();
        for (int row = fromRow; row <= toRow; ++row) {
            for (int col = fromCol; col <= toCol; ++col) {
                final double x = col;
                final double y = row;
                if (this.polygon.contains(x, y)) {
                    vertexList.add(new double[] { x, y });
                }
            }
        }
        final Graph graph = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        Square.handleDiagonals(graph, fromRow, toRow, fromCol, toCol, this.diagonals, 1.0E-4);
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
