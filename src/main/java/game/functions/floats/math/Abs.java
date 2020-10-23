// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.floats.math;

import game.Game;
import game.functions.floats.BaseFloatFunction;
import game.functions.floats.FloatFunction;
import util.Context;

public final class Abs extends BaseFloatFunction
{
    private static final long serialVersionUID = 1L;
    protected final FloatFunction value;
    
    public Abs(final FloatFunction value) {
        this.value = value;
    }
    
    @Override
    public float eval(final Context context) {
        return Math.abs(this.value.eval(context));
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.value.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.value.preprocess(game);
    }
}
