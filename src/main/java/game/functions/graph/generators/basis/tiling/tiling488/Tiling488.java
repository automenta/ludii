// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.basis.tiling.tiling488;

import annotations.Hide;
import game.Game;
import game.functions.graph.generators.basis.Basis;

import java.awt.geom.Point2D;

@Hide
public class Tiling488 extends Basis
{
    private static final long serialVersionUID = 1L;
    private static final double u = 1.0;
    private static final double v;
    public static final double[][] ref;
    
    public static Point2D.Double xy(final int row, final int col) {
        return new Point2D.Double(col * Tiling488.v, row * Tiling488.v);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        v = 1.0 * (1.0 + 2.0 / Math.sqrt(2.0));
        ref = new double[][] { { 0.5, Tiling488.v / 2.0 }, { Tiling488.v / 2.0, 0.5 }, { Tiling488.v / 2.0, -0.5 }, { 0.5, -Tiling488.v / 2.0 }, { -0.5, -Tiling488.v / 2.0 }, { -Tiling488.v / 2.0, -0.5 }, { -Tiling488.v / 2.0, 0.5 }, { -0.5, Tiling488.v / 2.0 } };
    }
}
