// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling3636;

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
import math.MathRoutines;
import math.Polygon;
import util.Context;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class CustomOn3636 extends Basis
{
    private static final long serialVersionUID = 1L;
    private final Polygon polygon;
    private final TIntArrayList sides;
    
    public CustomOn3636(final Polygon polygon) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        this.basis = BasisType.Hexagonal;
        this.shape = ShapeType.Custom;
        this.polygon.setFrom(polygon);
    }
    
    public CustomOn3636(final DimFunction[] sides) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        this.basis = BasisType.Hexagonal;
        this.shape = ((sides.length == 2 && sides[0].eval() == sides[1].eval() - 1) ? ShapeType.Limping : ShapeType.Custom);
        for (DimFunction side : sides) {
            this.sides.add(side.eval());
        }
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        if (this.polygon.isEmpty() && !this.sides.isEmpty()) {
            this.polygonFromSides();
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
                final Point2D ptRef = Tiling3636.xy(r, c);
                if (this.polygon.contains(ptRef)) {
                    for (int n = 0; n < Tiling3636.ref.length; ++n) {
                        final double x = ptRef.getX() + Tiling3636.ref[n][1];
                        final double y = ptRef.getY() + Tiling3636.ref[n][0];
                        int vid;
                        for (vid = 0; vid < vertexList.size(); ++vid) {
                            final double[] ptV = vertexList.get(vid);
                            final double dist = MathRoutines.distance(ptV[0], ptV[1], x, y);
                            if (dist < 0.1) {
                                break;
                            }
                        }
                        if (vid >= vertexList.size()) {
                            vertexList.add(new double[] { x, y });
                        }
                    }
                }
            }
        }
        final Graph graph = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        graph.reorder();
        return graph;
    }
    
    void polygonFromSides() {
        final int[][] steps = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 0 }, { -1, -1 }, { 0, -1 } };
        int step = 1;
        int row = 0;
        int col = 0;
        this.polygon.clear();
        this.polygon.add(Tiling3636.xy(row, col));
        for (int n = 0; n < Math.max(5, this.sides.size()); ++n) {
            int nextStep = this.sides.get(n % this.sides.size());
            if (nextStep < 0) {
                ++nextStep;
            }
            else {
                --nextStep;
            }
            if (nextStep < 0) {
                --step;
            }
            else {
                ++step;
            }
            step = (step + 6) % 6;
            if (nextStep > 0) {
                row += nextStep * steps[step][0];
                col += nextStep * steps[step][1];
                this.polygon.add(Tiling3636.xy(row, col));
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
