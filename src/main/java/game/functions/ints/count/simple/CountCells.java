// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.simple;

import annotations.Hide;
import game.Game;
import game.functions.ints.BaseIntFunction;
import util.Context;

@Hide
public final class CountCells extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private Integer preComputedInteger;
    
    public CountCells() {
        this.preComputedInteger = null;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.preComputedInteger != null) {
            return this.preComputedInteger;
        }
        return context.game().board().topology().cells().size();
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public String toString() {
        return "Cells()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 33554432L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.preComputedInteger = this.eval(new Context(game, null));
    }
}
