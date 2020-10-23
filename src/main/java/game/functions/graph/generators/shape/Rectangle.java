// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph.generators.shape;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.dim.DimFunction;
import game.functions.graph.GraphFunction;
import game.functions.graph.generators.basis.square.DiagonalsType;
import game.functions.graph.generators.basis.square.RectangleOnSquare;

public class Rectangle extends Shape
{
    private static final long serialVersionUID = 1L;
    
    public static GraphFunction construct(final DimFunction dimA, @Opt final DimFunction dimB, @Opt @Name final DiagonalsType diagonals) {
        return new RectangleOnSquare(dimA, (dimB == null) ? dimA : dimB, diagonals, null);
    }
    
    private Rectangle() {
        super(null, null, null, null);
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
