// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.dim.math;

import game.functions.dim.BaseDimFunction;
import game.functions.dim.DimFunction;

public final class Abs extends BaseDimFunction
{
    private static final long serialVersionUID = 1L;
    protected final DimFunction value;
    
    public Abs(final DimFunction value) {
        this.value = value;
    }
    
    @Override
    public int eval() {
        return Math.abs(this.value.eval());
    }
}
