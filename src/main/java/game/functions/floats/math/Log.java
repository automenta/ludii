// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.floats.math;

import game.Game;
import game.functions.floats.BaseFloatFunction;
import game.functions.floats.FloatFunction;
import util.Context;

public final class Log extends BaseFloatFunction
{
    private static final long serialVersionUID = 1L;
    private final FloatFunction a;
    
    public Log(final FloatFunction a) {
        this.a = a;
    }
    
    @Override
    public float eval(final Context context) {
        final float value = this.a.eval(context);
        if (value == 0.0f) {
            throw new IllegalArgumentException("Logarithm of zero is undefined.");
        }
        return (float)Math.log(value);
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flag = 0L;
        if (this.a != null) {
            flag |= this.a.gameFlags(game);
        }
        return flag;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.a != null) {
            this.a.preprocess(game);
        }
    }
}
