// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.floats;

import game.Game;
import util.Context;

public final class FloatConstant extends BaseFloatFunction
{
    private static final long serialVersionUID = 1L;
    private final float a;
    
    public FloatConstant(final float a) {
        this.a = a;
    }
    
    @Override
    public float eval(final Context context) {
        return this.a;
    }
    
    @Override
    public String toString() {
        final String str = "" + this.a;
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
