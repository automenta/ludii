// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling488;

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
public class CustomOn488 extends Basis
{
    private static final long serialVersionUID = 1L;
    private final Polygon polygon;
    private final TIntArrayList sides;
    
    public CustomOn488(final Polygon polygon) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        this.basis = BasisType.T488;
        this.shape = ShapeType.Custom;
        this.polygon.setFrom(polygon);
    }
    
    public CustomOn488(final DimFunction[] sides) {
        this.polygon = new Polygon();
        this.sides = new TIntArrayList();
        this.basis = BasisType.T488;
        this.shape = ShapeType.Custom;
        for (DimFunction side : sides) {
            this.sides.add(side.eval());
        }
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int[][] steps = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        if (this.polygon.isEmpty() && !this.sides.isEmpty()) {
            this.polygon.fromSides(this.sides, steps);
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
                final Point2D.Double ptRef = Tiling488.xy(r, c);
                if (this.polygon.contains(ptRef)) {
                    for (int n = 0; n < Tiling488.ref.length; ++n) {
                        final double x = ptRef.getX() + Tiling488.ref[n][0];
                        final double y = ptRef.getY() + Tiling488.ref[n][1];
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
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
