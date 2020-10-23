// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.hex;

import annotations.Hide;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.BaseGraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import math.MathRoutines;
import util.Context;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Hide
public class StarOnHex extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public StarOnHex(final DimFunction dim) {
        this.basis = BasisType.Hexagonal;
        this.shape = ShapeType.Star;
        this.dim = new int[] { dim.eval() };
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int d = this.dim[0];
        final int rows = 4 * this.dim[0] + 1;
        final int cols = 4 * this.dim[0] + 1;
        final List<double[]> vertexList = new ArrayList<>();
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                if (r < d) {
                    if (c < d) {
                        continue;
                    }
                    if (c - r > d) {
                        continue;
                    }
                }
                else if (r <= 2 * d) {
                    if (r - c > d) {
                        continue;
                    }
                    if (c >= cols - d) {
                        continue;
                    }
                }
                else if (r <= 3 * d) {
                    if (c < d) {
                        continue;
                    }
                    if (c - r > d) {
                        continue;
                    }
                }
                else {
                    if (c > 3 * d) {
                        continue;
                    }
                    if (r - c > d) {
                        continue;
                    }
                }
                final Point2D ptRef = Hex.xy(r, c);
                for (int n = 0; n < 6; ++n) {
                    final double x = ptRef.getX() + Hex.ref[n][0];
                    final double y = ptRef.getY() + Hex.ref[n][1];
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
