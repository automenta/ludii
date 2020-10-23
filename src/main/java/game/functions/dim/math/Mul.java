// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.dim.math;

import annotations.Alias;
import game.functions.dim.BaseDimFunction;
import game.functions.dim.DimFunction;

@Alias(alias = "*")
public final class Mul extends BaseDimFunction
{
    private static final long serialVersionUID = 1L;
    private final DimFunction a;
    private final DimFunction b;
    private final DimFunction[] list;
    
    public Mul(final DimFunction a, final DimFunction b) {
        this.a = a;
        this.b = b;
        this.list = null;
    }
    
    public Mul(final DimFunction[] list) {
        this.list = list;
        this.a = null;
        this.b = null;
    }
    
    @Override
    public int eval() {
        if (this.list == null) {
            return this.a.eval() * this.b.eval();
        }
        int mul = 1;
        for (final DimFunction elem : this.list) {
            mul *= elem.eval();
        }
        return mul;
    }
}
