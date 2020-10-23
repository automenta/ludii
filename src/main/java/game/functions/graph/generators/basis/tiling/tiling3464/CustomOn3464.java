// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling3464;

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
import main.math.MathRoutines;
import main.math.Polygon;
import util.Context;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class CustomOn3464 extends Basis
{
    private static final long serialVersionUID = 1L;
    private final Polygon polygon;
    private final TIntArrayList sides;
    private static final double[][] ref;
    
    public CustomOn3464(final Polygon polygon) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        final double a = 1.0 + Math.sqrt(3.0) / 2.0;
        final double h = a / Math.cos(Math.toRadians(15.0));
        for (int n = 0; n < 12; ++n) {
            final double theta = Math.toRadians(15 + n * 30);
            CustomOn3464.ref[6 + n][0] = h * Math.cos(theta);
            CustomOn3464.ref[6 + n][1] = h * Math.sin(theta);
        }
        this.basis = BasisType.T3464;
        this.shape = ShapeType.Custom;
        this.polygon.setFrom(polygon);
    }
    
    public CustomOn3464(final DimFunction[] sides) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        final double a = 1.0 + Math.sqrt(3.0) / 2.0;
        final double h = a / Math.cos(Math.toRadians(15.0));
        for (int n = 0; n < 12; ++n) {
            final double theta = Math.toRadians(15 + n * 30);
            CustomOn3464.ref[6 + n][0] = h * Math.cos(theta);
            CustomOn3464.ref[6 + n][1] = h * Math.sin(theta);
        }
        this.basis = BasisType.T3464;
        this.shape = ((sides.length == 2 && sides[0].eval() == sides[1].eval() - 1) ? ShapeType.Limping : ShapeType.Custom);
        for (int n2 = 0; n2 < sides.length; ++n2) {
            this.sides.add(sides[n2].eval());
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
                final Point2D ptRef = xy(r, c);
                if (this.polygon.contains(ptRef)) {
                    for (int n = 0; n < CustomOn3464.ref.length; ++n) {
                        final double x = ptRef.getX() + CustomOn3464.ref[n][1];
                        final double y = ptRef.getY() + CustomOn3464.ref[n][0];
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
        final Graph result = BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
        result.reorder();
        return result;
    }
    
    void polygonFromSides() {
        final int[][] steps = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 0 }, { -1, -1 }, { 0, -1 } };
        int dirn = 1;
        int row = 0;
        int col = 0;
        this.polygon.clear();
        this.polygon.add(Tiling3464.xy(row, col));
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
                this.polygon.add(xy(row, col));
            }
        }
    }
    
    static Point2D xy(final int row, final int col) {
        final double hx = 1.0 * (1.0 + Math.sqrt(3.0));
        final double hy = 1.0 * (3.0 + Math.sqrt(3.0)) / 2.0;
        return new Point2D.Double(hx * (col - 0.5 * row), hy * row);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        ref = new double[][] { { -0.5, 1.0 * Tiling3464.uy }, { 0.5, 1.0 * Tiling3464.uy }, { 1.0, 0.0 * Tiling3464.uy }, { 0.5, -1.0 * Tiling3464.uy }, { -0.5, -1.0 * Tiling3464.uy }, { -1.0, 0.0 * Tiling3464.uy }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 } };
    }
}
