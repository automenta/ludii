// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.shape;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.BaseGraphFunction;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.util.graph.Graph;
import util.Context;

public class Shape extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    
    public Shape(@Opt final ShapeStarType star, final DimFunction numSides) {
        this.basis = BasisType.NoBasis;
        this.shape = ((star != null) ? ShapeType.Star : ShapeType.Polygon);
        this.dim = new int[] { numSides.eval() };
    }
    
    @Hide
    public Shape(final BasisType basis, final ShapeType shape, final DimFunction dimA, @Opt final DimFunction dimB) {
        this.basis = basis;
        this.shape = shape;
        if (dimB == null) {
            this.dim = new int[] { dimA.eval() };
        }
        else {
            this.dim = new int[] { dimA.eval(), dimB.eval() };
        }
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        final int numSides = this.dim[0];
        final double r = numSides / 6.283185307179586;
        final Graph graph = new Graph();
        final double offset = (numSides == 4) ? 0.7853981633974483 : 1.5707963267948966;
        for (int n = 0; n < numSides; ++n) {
            final double theta = offset + n / (double)numSides * 2.0 * 3.141592653589793;
            final double x = r * Math.cos(theta);
            final double y = r * Math.sin(theta);
            graph.addVertex(x, y);
        }
        if (this.shape == ShapeType.Star) {
            for (int n = 0; n < numSides; ++n) {
                graph.addEdge(n, (n + (numSides - 1) / 2) % numSides);
            }
        }
        else {
            for (int n = 0; n < numSides; ++n) {
                graph.addEdge(n, (n + 1) % numSides);
            }
        }
        if (siteType == SiteType.Cell) {
            graph.makeFaces(true);
        }
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
