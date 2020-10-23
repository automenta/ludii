// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.dim.math;

import game.functions.dim.BaseDimFunction;
import game.functions.dim.DimFunction;

public final class Max extends BaseDimFunction
{
    private static final long serialVersionUID = 1L;
    private final DimFunction valueA;
    private final DimFunction valueB;
    
    public Max(final DimFunction valueA, final DimFunction valueB) {
        this.valueA = valueA;
        this.valueB = valueB;
    }
    
    @Override
    public int eval() {
        return Math.max(this.valueA.eval(), this.valueB.eval());
    }
}
