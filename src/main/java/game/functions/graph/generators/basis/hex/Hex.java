// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.hex;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.dim.DimConstant;
import game.functions.dim.DimFunction;
import game.functions.dim.math.Add;
import game.functions.graph.GraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Poly;
import util.Context;

import java.awt.geom.Point2D;

public class Hex extends Basis
{
    private static final long serialVersionUID = 1L;
    public static final double ux;
    public static final double uy = 1.0;
    public static final double[][] ref;
    
    public static GraphFunction construct(@Opt final HexShapeType shape, final DimFunction dimA, @Opt final DimFunction dimB) {
        final HexShapeType st = (shape == null) ? HexShapeType.Hexagon : shape;
        switch (st) {
            case Hexagon: {
                return new HexagonOnHex(dimA);
            }
            case Triangle: {
                return new TriangleOnHex(dimA);
            }
            case Diamond: {
                return new DiamondOnHex(dimA, null);
            }
            case Prism: {
                return new DiamondOnHex(dimA, (dimB != null) ? dimB : dimA);
            }
            case Star: {
                return new StarOnHex(dimA);
            }
            case Limping: {
                final DimFunction dimAplus1 = new Add(dimA, new DimConstant(1));
                return new CustomOnHex(new DimFunction[] { dimA, dimAplus1 });
            }
            case Square: {
                return new RectangleOnHex(dimA, dimA);
            }
            case Rectangle: {
                return new RectangleOnHex(dimA, (dimB != null) ? dimB : dimA);
            }
            default: {
                throw new IllegalArgumentException("Shape " + st + " not supported for hex tiling.");
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
            return new CustomOnHex(poly.polygon());
        }
        return new CustomOnHex(sides);
    }
    
    private Hex() {
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        return null;
    }
    
    public static Point2D xy(final int row, final int col) {
        final double hx = 1.0 * Math.sqrt(3.0);
        final double hy = 1.5;
        return new Point2D.Double(hx * (col - 0.5 * row), 1.5 * row);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        ux = Math.sqrt(3.0) / 2.0;
        ref = new double[][] { { 0.0 * Hex.ux, 1.0 }, { 1.0 * Hex.ux, 0.5 }, { 1.0 * Hex.ux, -0.5 }, { 0.0 * Hex.ux, -1.0 }, { -1.0 * Hex.ux, -0.5 }, { -1.0 * Hex.ux, 0.5 } };
    }
}
