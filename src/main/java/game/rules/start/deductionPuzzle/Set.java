// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.deductionPuzzle;

import annotations.Opt;
import game.Game;
import game.rules.start.StartRule;
import game.types.board.SiteType;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.puzzle.ActionSet;

public final class Set extends StartRule
{
    private static final long serialVersionUID = 1L;
    protected final Integer[] vars;
    protected final Integer[] values;
    protected final SiteType type;
    
    public Set(@Opt final SiteType type, final Integer[]... pairs) {
        if (pairs == null) {
            this.values = null;
            this.vars = null;
        }
        else {
            this.values = new Integer[pairs.length];
            this.vars = new Integer[pairs.length];
            for (int n = 0; n < pairs.length; ++n) {
                this.vars[n] = pairs[n][0];
                this.values[n] = pairs[n][1];
            }
        }
        this.type = type;
    }
    
    @Override
    public void eval(final Context context) {
        final SiteType realType = (this.type == null) ? context.board().defaultSite() : this.type;
        for (int minSize = Math.min(this.vars.length, this.values.length), i = 0; i < minSize; ++i) {
            final BaseAction actionAtomic = new ActionSet(realType, this.vars[i], this.values[i]);
            actionAtomic.apply(context, true);
            context.trial().moves().add(new Move(actionAtomic));
            context.trial().addInitPlacement();
        }
    }
    
    public Integer[] vars() {
        return this.vars;
    }
    
    public Integer[] values() {
        return this.values;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 128L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        String str = "(set ";
        for (int minSize = Math.min(this.vars.length, this.values.length), i = 0; i < minSize; ++i) {
            str = str + this.values[i] + " on " + this.vars[i] + " ";
        }
        str += ")";
        return str;
    }
    
    @Override
    public boolean isSet() {
        return true;
    }
}
