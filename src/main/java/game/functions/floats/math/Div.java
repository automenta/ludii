// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.floats.math;

import annotations.Alias;
import game.Game;
import game.functions.floats.BaseFloatFunction;
import game.functions.floats.FloatFunction;
import util.Context;

@Alias(alias = "/")
public final class Div extends BaseFloatFunction
{
    private static final long serialVersionUID = 1L;
    private final FloatFunction a;
    private final FloatFunction b;
    
    public Div(final FloatFunction a, final FloatFunction b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public float eval(final Context context) {
        final float evalB = this.b.eval(context);
        if (evalB == 0.0f) {
            throw new IllegalArgumentException("Division by zero.");
        }
        return this.a.eval(context) / evalB;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flag = 0L;
        if (this.a != null) {
            flag |= this.a.gameFlags(game);
        }
        if (this.b != null) {
            flag |= this.b.gameFlags(game);
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
        if (this.b != null) {
            this.b.preprocess(game);
        }
    }
}
