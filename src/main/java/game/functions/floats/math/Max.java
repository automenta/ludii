// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.floats.math;

import game.Game;
import game.functions.floats.BaseFloatFunction;
import game.functions.floats.FloatFunction;
import util.Context;

public final class Max extends BaseFloatFunction
{
    private static final long serialVersionUID = 1L;
    private final FloatFunction a;
    private final FloatFunction b;
    protected final FloatFunction[] list;
    
    public Max(final FloatFunction a, final FloatFunction b) {
        this.a = a;
        this.b = b;
        this.list = null;
    }
    
    public Max(final FloatFunction[] list) {
        this.a = null;
        this.b = null;
        this.list = list;
    }
    
    @Override
    public float eval(final Context context) {
        if (this.list == null) {
            return Math.max(this.a.eval(context), this.b.eval(context));
        }
        float max = this.list[0].eval(context);
        for (int i = 1; i < this.list.length; ++i) {
            max = Math.max(max, this.list[i].eval(context));
        }
        return max;
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
        if (this.list != null) {
            for (final FloatFunction elem : this.list) {
                flag |= elem.gameFlags(game);
            }
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
        if (this.list != null) {
            for (final FloatFunction elem : this.list) {
                elem.preprocess(game);
            }
        }
    }
}
