// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.simple;

import annotations.Hide;
import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

@Hide
public final class CountPhases extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private Integer preComputedInteger;
    
    public CountPhases() {
        this.preComputedInteger = null;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.preComputedInteger != null) {
            return this.preComputedInteger;
        }
        return context.game().rules().phases().length;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public String toString() {
        return "Phases()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.preComputedInteger = this.eval(new Context(game, null));
    }
}
