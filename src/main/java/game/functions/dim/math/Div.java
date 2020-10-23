// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.dim.math;

import annotations.Alias;
import game.functions.dim.BaseDimFunction;
import game.functions.dim.DimFunction;

@Alias(alias = "/")
public final class Div extends BaseDimFunction
{
    private static final long serialVersionUID = 1L;
    private final DimFunction a;
    private final DimFunction b;
    
    public Div(final DimFunction a, final DimFunction b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public int eval() {
        final int evalB = this.b.eval();
        if (evalB == 0) {
            throw new IllegalArgumentException("Division by zero.");
        }
        return this.a.eval() / evalB;
    }
}
