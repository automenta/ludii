// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.dim.math;

import annotations.Alias;
import game.functions.dim.BaseDimFunction;
import game.functions.dim.DimFunction;

@Alias(alias = "-")
public final class Sub extends BaseDimFunction
{
    private static final long serialVersionUID = 1L;
    private final DimFunction valueA;
    private final DimFunction valueB;
    
    public Sub(final DimFunction valueA, final DimFunction valueB) {
        this.valueA = valueA;
        this.valueB = valueB;
    }
    
    @Override
    public int eval() {
        return this.valueA.eval() - this.valueB.eval();
    }
}
