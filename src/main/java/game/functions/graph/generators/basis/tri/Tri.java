// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tri;

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

public class Tri extends Basis
{
    private static final long serialVersionUID = 1L;
    
    public static GraphFunction construct(@Opt final TriShapeType shape, final DimFunction dimA, @Opt final DimFunction dimB) {
        final TriShapeType st = (shape == null) ? TriShapeType.Triangle : shape;
        switch (st) {
            case Hexagon -> {
                return new HexagonOnTri(dimA);
            }
            case Triangle -> {
                return new TriangleOnTri(dimA);
            }
            case Diamond -> {
                return new DiamondOnTri(dimA, null);
            }
            case Prism -> {
                return new DiamondOnTri(dimA, (dimB != null) ? dimB : dimA);
            }
            case Square -> {
                return new RectangleOnTri(dimA, dimA);
            }
            case Rectangle -> {
                return new RectangleOnTri(dimA, (dimB != null) ? dimB : dimA);
            }
            case Star -> {
                return new StarOnTri(dimA);
            }
            case Limping -> {
                final DimFunction dimAplus1 = new Add(dimA, new DimConstant(1));
                return new CustomOnTri(new DimFunction[]{dimA, dimAplus1});
            }
            default -> {
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
            return new CustomOnTri(poly.polygon());
        }
        return new CustomOnTri(sides);
    }
    
    private Tri() {
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        return null;
    }
    
    public static Point2D xy(final int row, final int col) {
        final double hx = 1.0;
        final double hy = Math.sqrt(3.0) / 2.0;
        return new Point2D.Double(1.0 * (col - 0.5 * row), hy * row);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
