// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling3464;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.dim.DimConstant;
import game.functions.dim.DimFunction;
import game.functions.dim.math.Add;
import game.functions.graph.GraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.util.graph.Poly;

import java.awt.geom.Point2D;

@Hide
public class Tiling3464 extends Basis
{
    private static final long serialVersionUID = 1L;
    public static final double ux = 1.0;
    public static final double uy;
    public static final double[][] ref;
    
    public static GraphFunction construct(@Opt final Tiling3464ShapeType shape, final DimFunction dimA, @Opt final DimFunction dimB) {
        final Tiling3464ShapeType st = (shape == null) ? Tiling3464ShapeType.Hexagon : shape;
        switch (st) {
            case Hexagon: {
                return new HexagonOn3464(dimA);
            }
            case Triangle: {
                return new TriangleOn3464(dimA);
            }
            case Diamond: {
                return new DiamondOn3464(dimA, null);
            }
            case Prism: {
                return new DiamondOn3464(dimA, (dimB != null) ? dimB : dimA);
            }
            case Star: {
                return new StarOn3464(dimA);
            }
            case Limping: {
                final DimFunction dimAplus1 = new Add(dimA, new DimConstant(1));
                return new CustomOn3464(new DimFunction[] { dimA, dimAplus1 });
            }
            case Square: {
                return new RectangleOn3464(dimA, dimA);
            }
            case Rectangle: {
                return new RectangleOn3464(dimA, (dimB != null) ? dimB : dimA);
            }
            default: {
                throw new IllegalArgumentException("Shape " + st + " not supported for tiling3464.");
            }
        }
    }
    
    public static GraphFunction construct(@Or final Poly poly, @Or final DimFunction[] sides) {
        int numNonNull = 0;
        if (poly != null) {
            ++numNonNull;
        }
        if (sides != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Exactly one array parameter must be non-null.");
        }
        if (poly != null) {
            return new CustomOn3464(poly.polygon());
        }
        return new CustomOn3464(sides);
    }
    
    private Tiling3464() {
        final double a = 1.0 + Math.sqrt(3.0) / 2.0;
        final double h = a / Math.cos(Math.toRadians(15.0));
        for (int n = 0; n < 12; ++n) {
            final double theta = Math.toRadians(15 + n * 30);
            Tiling3464.ref[6 + n][0] = h * Math.cos(theta);
            Tiling3464.ref[6 + n][1] = h * Math.sin(theta);
        }
    }
    
    public static Point2D xy(final int row, final int col) {
        final double hx = 1.0 * (1.0 + Math.sqrt(3.0));
        final double hy = 1.0 * (3.0 + Math.sqrt(3.0)) / 2.0;
        return new Point2D.Double(hx * (col - row), hy * (row + col) * 0.5);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        uy = 1.0 * Math.sqrt(3.0) / 2.0;
        ref = new double[][] { { -0.5, 1.0 * Tiling3464.uy }, { 0.5, 1.0 * Tiling3464.uy }, { 1.0, 0.0 * Tiling3464.uy }, { 0.5, -1.0 * Tiling3464.uy }, { -0.5, -1.0 * Tiling3464.uy }, { -1.0, 0.0 * Tiling3464.uy }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 0.0 } };
    }
}
