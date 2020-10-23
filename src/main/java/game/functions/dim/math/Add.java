// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.dim.math;

import annotations.Alias;
import game.functions.dim.BaseDimFunction;
import game.functions.dim.DimFunction;

@Alias(alias = "+")
public final class Add extends BaseDimFunction
{
    private static final long serialVersionUID = 1L;
    private final DimFunction a;
    private final DimFunction b;
    protected final DimFunction[] list;
    
    public Add(final DimFunction a, final DimFunction b) {
        this.a = a;
        this.b = b;
        this.list = null;
    }
    
    public Add(final DimFunction[] list) {
        this.a = null;
        this.b = null;
        this.list = list;
    }
    
    @Override
    public int eval() {
        if (this.list == null) {
            return this.a.eval() + this.b.eval();
        }
        int sum = 0;
        for (final DimFunction elem : this.list) {
            sum += elem.eval();
        }
        return sum;
    }
}
