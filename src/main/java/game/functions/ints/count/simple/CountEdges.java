// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.simple;

import annotations.Hide;
import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

@Hide
public final class CountEdges extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private Integer preComputedInteger;
    
    public CountEdges() {
        this.preComputedInteger = null;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.preComputedInteger != null) {
            return this.preComputedInteger;
        }
        return context.game().board().topology().edges().size();
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public String toString() {
        return "Edges()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 67108864L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.preComputedInteger = this.eval(new Context(game, null));
    }
}
