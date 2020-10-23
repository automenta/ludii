// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.floats.math;

import game.Game;
import game.functions.floats.BaseFloatFunction;
import game.functions.floats.FloatFunction;
import util.Context;

public final class Cos extends BaseFloatFunction
{
    private static final long serialVersionUID = 1L;
    private final FloatFunction a;
    
    public Cos(final FloatFunction a) {
        this.a = a;
    }
    
    @Override
    public float eval(final Context context) {
        return (float)Math.cos(this.a.eval(context));
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
