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
import main.math.MathRoutines;
import util.Context;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class SquareOrRectangleOn488 extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public SquareOrRectangleOn488(final DimFunction dimA, final DimFunction dimB) {
        this.basis = BasisType.T488;
        this.shape = ((dimB == null || dimA == dimB) ? ShapeType.Square : ShapeType.Rectangle);
        if (dimB == null || dimA == dimB) {
            this.dim = new int[] { dimA.eval() };
        }
        else {
            this.dim = new int[] { dimA.eval(), dimB.eval() };
        }
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int rows = this.dim[0];
        final int cols = (this.dim.length > 1) ? this.dim[1] : this.dim[0];
        final List<double[]> vertexList = new ArrayList<>();
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                final Point2D ptRef = Tiling488.xy(r, c);
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
        return BaseGraphFunction.createGraphFromVertexList(vertexList, 1.0, this.basis, this.shape);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
