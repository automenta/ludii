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
import gnu.trove.list.array.TIntArrayList;
import math.Polygon;
import util.Context;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class CustomOnTri extends Basis
{
    private static final long serialVersionUID = 1L;
    private final Polygon polygon;
    private final TIntArrayList sides;
    
    public CustomOnTri(final Polygon polygon) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        this.basis = BasisType.Triangular;
        this.shape = ShapeType.Custom;
        this.polygon.setFrom(polygon);
    }
    
    public CustomOnTri(final DimFunction[] sides) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        this.basis = BasisType.Triangular;
        this.shape = ((sides.length == 2 && sides[0].eval() == sides[1].eval() - 1) ? ShapeType.Limping : ShapeType.Custom);
        for (DimFunction side : sides) {
            this.sides.add(side.eval());
        }
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.polygon.isEmpty() && !this.sides.isEmpty()) {
            this.polygonFromSides(siteType);
        }
        this.polygon.inflate(0.1);
        final Rectangle2D bounds = this.polygon.bounds();
        final int fromCol = (int)bounds.getMinX() - 2;
        final int fromRow = (int)bounds.getMinY() - 2;
        final int toCol = (int)bounds.getMaxX() + 2;
        final int toRow = (int)bounds.getMaxY() + 2;
        final List<double[]> vertexList = new ArrayList<>();
        for (int r = fromRow; r <= toRow; ++r) {
            for (int c = fromCol; c <= toCol; ++c) {
                final Point2D pt = Tri.xy(r, c);
                if (this.polygon.contains(pt)) {
                    vertexList.add(new double[] { pt.getX(), pt.getY() });
                }
            }
        }
        final Graph graph = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        graph.reorder();
        return graph;
    }
    
    void polygonFromSides(final SiteType siteType) {
        final int[][] steps = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 0 }, { -1, -1 }, { 0, -1 } };
        int dirn = 1;
        int row = 0;
        int col = 0;
        this.polygon.clear();
        this.polygon.add(Tri.xy(row, col));
        for (int n = 0; n < Math.max(5, this.sides.size()); ++n) {
            int nextStep = this.sides.get(n % this.sides.size());
            if (nextStep < 0) {
                ++nextStep;
            }
            else {
                --nextStep;
            }
            if (nextStep < 0) {
                --dirn;
            }
            else {
                ++dirn;
            }
            dirn = (dirn + 6) % 6;
            if (nextStep > 0) {
                row += nextStep * steps[dirn][0];
                col += nextStep * steps[dirn][1];
                this.polygon.add(Tri.xy(row, col));
            }
        }
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
