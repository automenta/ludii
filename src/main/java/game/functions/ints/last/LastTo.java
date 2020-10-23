// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.last;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.BaseIntFunction;
import util.Context;
import util.Move;

@Hide
public final class LastTo extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final BooleanFunction afterSubsequentsFn;
    
    public LastTo(@Opt @Name final BooleanFunction afterSubsequents) {
        this.afterSubsequentsFn = ((afterSubsequents == null) ? BooleanConstant.construct(false) : afterSubsequents);
    }
    
    @Override
    public int eval(final Context context) {
        final Move move = context.trial().lastMove();
        if (this.afterSubsequentsFn.eval(context)) {
            if (move == null) {
                return -1;
            }
            return move.toAfterSubsequents();
        }
        else {
            if (move == null) {
                return -1;
            }
            return move.toNonDecision();
        }
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.afterSubsequentsFn.preprocess(game);
    }
    
    @Override
    public String toString() {
        final String str = "(LastTo)";
        return "(LastTo)";
    }
}
