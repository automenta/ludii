// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.dim;

public final class DimConstant extends BaseDimFunction
{
    private static final long serialVersionUID = 1L;
    private final int a;
    
    public DimConstant(final int a) {
        this.a = a;
    }
    
    @Override
    public int eval() {
        return this.a;
    }
    
    @Override
    public String toString() {
        final String str = "" + this.a;
        return str;
    }
}
