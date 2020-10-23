// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.end;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanFunction;
import util.Context;

public class If extends EndRule
{
    private static final long serialVersionUID = 1L;
    private final BooleanFunction endCondition;
    private final If[] subconditions;
    
    public If(final BooleanFunction test, @Opt @Or final If sub, @Opt @Or final If[] subs, @Opt final Result result) {
        super(result);
        int numNonNull = 0;
        if (sub != null) {
            ++numNonNull;
        }
        if (subs != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Can't have more than one non-null Or parameter.");
        }
        this.endCondition = test;
        this.subconditions = (subs != null) ? subs : ((sub != null) ? new If[] { sub } : null);
    }
    
    @Override
    public EndRule eval(final Context context) {
        if (!this.endCondition.eval(context)) {
            return null;
        }
        if (this.subconditions == null) {
            this.result().eval(context);
            return new BaseEndRule(this.result());
        }
        for (final If sub : this.subconditions) {
            final EndRule subResult = sub.eval(context);
            if (subResult != null) {
                return subResult;
            }
        }
        return new BaseEndRule(this.result());
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        if (this.endCondition != null) {
            gameFlags |= this.endCondition.gameFlags(game);
        }
        if (this.subconditions != null) {
            for (final If sub : this.subconditions) {
                gameFlags |= sub.gameFlags(game);
            }
        }
        if (this.result() != null) {
            gameFlags |= this.result().gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.endCondition != null) {
            this.endCondition.preprocess(game);
        }
        if (this.subconditions != null) {
            for (final If sub : this.subconditions) {
                sub.preprocess(game);
            }
        }
        if (this.result() != null) {
            this.result().preprocess(game);
        }
    }
    
    public BooleanFunction endCondition() {
        return this.endCondition;
    }
}
