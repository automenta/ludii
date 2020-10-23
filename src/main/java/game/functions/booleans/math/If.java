// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.math;

import annotations.Opt;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import util.Context;

public final class If extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final BooleanFunction cond;
    private final BooleanFunction ok;
    private final BooleanFunction notOk;
    
    public If(final BooleanFunction cond, final BooleanFunction ok, @Opt final BooleanFunction notOk) {
        this.cond = cond;
        this.ok = ok;
        this.notOk = notOk;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.cond.eval(context)) {
            return this.ok.eval(context);
        }
        return this.notOk != null && this.notOk.eval(context);
    }
    
    public BooleanFunction cond() {
        return this.cond;
    }
    
    public BooleanFunction ok() {
        return this.ok;
    }
    
    public BooleanFunction notOk() {
        return this.notOk;
    }
    
    @Override
    public boolean isStatic() {
        return (this.ok == null || this.ok.isStatic()) && (this.notOk == null || this.notOk.isStatic()) && this.cond.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long stateFlag = this.cond.gameFlags(game);
        if (this.ok != null) {
            stateFlag |= this.ok.gameFlags(game);
        }
        if (this.notOk != null) {
            stateFlag |= this.notOk.gameFlags(game);
        }
        return stateFlag;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.cond.preprocess(game);
        if (this.ok != null) {
            this.ok.preprocess(game);
        }
        if (this.notOk != null) {
            this.notOk.preprocess(game);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[If ");
        sb.append("cond=").append(this.cond);
        sb.append(", ok=").append(this.ok);
        sb.append(", notOk").append(this.notOk);
        sb.append("]");
        return sb.toString();
    }
}
