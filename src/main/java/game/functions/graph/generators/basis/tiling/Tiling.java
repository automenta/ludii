// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.dim.DimConstant;
import game.functions.dim.DimFunction;
import game.functions.graph.GraphFunction;
import game.functions.graph.generators.basis.Basis;
import game.functions.graph.generators.basis.tiling.tiling31212.Tiling31212;
import game.functions.graph.generators.basis.tiling.tiling333333_33434.Tiling333333_33434;
import game.functions.graph.generators.basis.tiling.tiling33336.Tiling33336;
import game.functions.graph.generators.basis.tiling.tiling33344.CustomOn33344;
import game.functions.graph.generators.basis.tiling.tiling33344.Tiling33344;
import game.functions.graph.generators.basis.tiling.tiling33434.Tiling33434;
import game.functions.graph.generators.basis.tiling.tiling3464.CustomOn3464;
import game.functions.graph.generators.basis.tiling.tiling3464.HexagonOn3464;
import game.functions.graph.generators.basis.tiling.tiling3464.ParallelogramOn3464;
import game.functions.graph.generators.basis.tiling.tiling3636.CustomOn3636;
import game.functions.graph.generators.basis.tiling.tiling3636.Tiling3636;
import game.functions.graph.generators.basis.tiling.tiling4612.Tiling4612;
import game.functions.graph.generators.basis.tiling.tiling488.CustomOn488;
import game.functions.graph.generators.basis.tiling.tiling488.SquareOrRectangleOn488;
import game.functions.graph.operators.Keep;
import game.types.board.SiteType;
import game.util.graph.Graph;
import game.util.graph.Poly;
import util.Context;

public class Tiling extends Basis
{
    private static final long serialVersionUID = 1L;
    
    @Hide
    private Tiling() {
    }
    
    public static GraphFunction construct(final TilingType tiling, final DimFunction dimA, @Opt final DimFunction dimB) {
        switch (tiling) {
            case T333333_33434 -> {
                return new Tiling333333_33434(dimA);
            }
            case T3636 -> {
                return new Tiling3636(dimA, dimB);
            }
            case T33344 -> {
                return new Tiling33344(dimA, dimB);
            }
            case T488 -> {
                return new SquareOrRectangleOn488(dimA, dimB);
            }
            case T3464 -> {
                if (dimB == null) {
                    return new HexagonOn3464(dimA);
                }
                return new ParallelogramOn3464(dimA, dimB);
            }
            case T4612 -> {
                return new Tiling4612(dimA);
            }
            case T31212 -> {
                return new Tiling31212(dimA);
            }
            case T33336 -> {
                return new Tiling33336(dimA);
            }
            case T33434 -> {
                return new Tiling33434(dimA);
            }
            default -> {
                return null;
            }
        }
    }
    
    public static GraphFunction construct(final TilingType tiling, @Or final Poly poly, @Or final DimFunction[] sides) {
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
        switch (tiling) {
            case T333333_33434 -> {
                return new Tiling333333_33434(new DimConstant(2));
            }
            case T3636 -> {
                return (poly != null) ? new CustomOn3636(poly.polygon()) : new CustomOn3636(sides);
            }
            case T3464 -> {
                return (poly != null) ? new CustomOn3464(poly.polygon()) : new CustomOn3464(sides);
            }
            case T33344 -> {
                return (poly != null) ? new CustomOn33344(poly.polygon()) : new CustomOn33344(sides);
            }
            case T488 -> {
                return (poly != null) ? new CustomOn488(poly.polygon()) : new CustomOn488(sides);
            }
            case T4612 -> {
                return new Tiling4612(new DimConstant(3));
            }
            case T31212 -> {
                return new Tiling31212(new DimConstant(3));
            }
            case T33336 -> {
                return new Tiling33336(new DimConstant(3));
            }
            case T33434 -> {
                if (poly == null) {
                    return new Tiling33434(new DimConstant(3));
                }
                return new Keep(new Tiling33434(new DimConstant(5)), poly);
            }
            default -> {
                return null;
            }
        }
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        return null;
    }
    
    @Override
    public String toEnglish() {
        return "<Shape>";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
